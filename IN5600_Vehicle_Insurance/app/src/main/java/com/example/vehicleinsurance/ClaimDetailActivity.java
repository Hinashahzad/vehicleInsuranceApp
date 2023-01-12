 package com.example.vehicleinsurance;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

 public class ClaimDetailActivity extends AppCompatActivity implements IAddEditListener {
    private static final String LOG_TAG = ClaimDetailActivity.class.getSimpleName();
    private static final Gson GSON = new Gson();
    private String claimString, numberOfClaims, userId;
    private ServerCommunicationInterface serverCommunicationInterface;
    private AddEditClaimFragment addEditClaimFragment;
    private ReadClaimFragment readClaimFragment;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_detail);

        Intent intent = getIntent();
        claimString = intent.getStringExtra(ClaimsOverviewActivity.CLAIM);
        userId = intent.getStringExtra(ClaimsOverviewActivity.USER_ID);
        numberOfClaims = intent.getStringExtra(ClaimsOverviewActivity.NUMBER_OF_CLAIMS);

        RequestQueue queue = Volley.newRequestQueue(this);
        serverCommunicationInterface = new ServerCommunicationInterface(queue, userId);

        if (claimString == null) {
            openEdit();
            return;
        }
        if (savedInstanceState == null) {
            openRead();
        }
    }

    private void openEdit() {
        addEditClaimFragment = AddEditClaimFragment.newInstance(claimString);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_view, addEditClaimFragment)
                .commit();
    }

    private void openReadFromClaim(Claim claim) {
        claimString = GSON.toJson(claim);
        sweetAlertDialog.cancel();
        openRead();
    }

    private void openRead() {
        readClaimFragment = ReadClaimFragment.newInstance(claimString);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_view, readClaimFragment)
                .commit();
    }

    public void onOpenMapClick(String latLng, boolean isEditable) {
        MapsFragment mapsFragment = MapsFragment.newInstance(latLng, isEditable);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true)
                .addToBackStack(null);

        if (isEditable)
            fragmentTransaction.hide(addEditClaimFragment);
        else
            fragmentTransaction.hide(readClaimFragment);

        fragmentTransaction
                .add(R.id.fragment_view, mapsFragment)
                .commit();
    }

    public void reopenEdit(String latitude, String longitude) {
        if (latitude != null && longitude != null)
            addEditClaimFragment.setLocationData(latitude, longitude);
        getSupportFragmentManager().popBackStack();
    }

    public void onEditClick(View view) {
        openEdit();
    }

    @Override
    public void onAddClick(Claim claim) {
        sweetAlertDialog = SweetDialogBoxHelper.showProgress(this);
        claim.id = numberOfClaims;
        SharedPreferencesHandler.updateClaim(getApplicationContext(), claim, userId);
        serverCommunicationInterface.addNewClaim(claim,
                success -> openReadFromClaim(claim),
                error -> onAddError(claim)
        );
    }

    @Override
    public void onSaveClick(Claim claim) {
        sweetAlertDialog = SweetDialogBoxHelper.showProgress(this);
        SharedPreferencesHandler.updateClaim(getApplicationContext(), claim, userId);
        serverCommunicationInterface.updateClaim(claim,
                success -> openReadFromClaim(claim),
                error -> onError(claim)
        );
    }

    private void onError(Claim claim) {
        SharedPreferencesHandler.setNeedServerUpdate(this, userId, claim.id);
        openReadFromClaim(claim);
    }

    private void onAddError(Claim claim) {
        SharedPreferencesHandler.setNeedServerAdd(this, userId, claim.id);
        openReadFromClaim(claim);
    }

    @Override
    public String imageChanged(Uri newImageUri, String claimId) {
        String fileName;
        if (claimId == null || claimId.isEmpty())
            fileName = "User_" + userId + "_Claim_" + numberOfClaims + ".jpg";
        else
            fileName = "User_" + userId + "_Claim_" + claimId + ".jpg";

        byte[] imageByteArray = new byte[]{};
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newImageUri);
            File path = new File(getApplicationContext().getFilesDir(), "VehicleInsurance" + File.separator + "Images");
            if (!path.exists())
                path.mkdirs();

            File outFile = new File(path, fileName);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageByteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverCommunicationInterface.uploadImage(imageByteArray, fileName,
                success -> {},
                error -> SharedPreferencesHandler.setNeedUploadImage(this, userId, fileName)
        );
        return fileName;
    }

    public void cancelLocation(View view) {
        reopenEdit(null, null);
    }
}