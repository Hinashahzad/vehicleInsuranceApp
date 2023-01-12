package com.example.vehicleinsurance;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClaimsOverviewActivity extends AppCompatActivity {
    private final Gson GSON = new Gson();
    private final String URL = "http://10.0.2.2:8080";
    private EditText currentPasswordEdit, newPasswordEdit, repeatPasswordEdit;
    private RequestQueue requestQueue;
    private AlertDialog dialog;

    private String email, md5pass, userId;
    private RecyclerView recyclerView;
    private ServerCommunicationInterface serverCommunicationInterface;
    private ClaimsListAdapter adapter;
    private SweetAlertDialog sweetAlertDialog;

    public static final String CLAIM = "com.example.vehicleinsurance.extra.CLAIM";
    public static final String USER_ID = "com.example.vehicleinsurance.extra.USER_ID";
    public static final String NUMBER_OF_CLAIMS = "com.example.vehicleinsurance.extra.NUMBER_OF_CLAIMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueue queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        userId = intent.getStringExtra(LoginActivity.USER_ID);
        md5pass = getIntent().getStringExtra(LoginActivity.PASSWORD);
        email = getIntent().getStringExtra(LoginActivity.EMAIL);
        sweetAlertDialog = SweetDialogBoxHelper.showProgress(this);

        serverCommunicationInterface = new ServerCommunicationInterface(queue, userId);
        setContentView(R.layout.activity_claims_overview);
        createEmptyRecyclerView();
    }

    private void createEmptyRecyclerView() {
        recyclerView = findViewById(R.id.claims_overview);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ClaimsListAdapter adapter = new ClaimsListAdapter(this, new Claim[]{});
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example_menue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_changePassword:
                openDialog();
                return true;
            case R.id.item_logOut:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        offlineSetClaims();
        serverCommunicationInterface.getClaims(success -> setClaims(success), error -> {});
        super.onResume();
    }

    public void openDialog() {
        View v = getLayoutInflater().inflate(R.layout.changepassword,null);

        currentPasswordEdit = v.findViewById(R.id.edit_currentPassword);
        newPasswordEdit = v.findViewById(R.id.edit_newPassword);
        repeatPasswordEdit = v.findViewById(R.id.edit_repeatPassword);

        AlertDialog.Builder alert = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog);
        alert.setView(v);
        alert.setCancelable(false);
        dialog = alert.create();
        dialog.getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xfff3f3f3));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
    }

    public void changePassword(View view){
        String currentPassword = currentPasswordEdit.getText().toString().trim();
        String newPassword = newPasswordEdit.getText().toString().trim();
        String repeatPassword = repeatPasswordEdit.getText().toString().trim();
        String md5CurrentPass = HashPassword.getMD5(currentPassword);
        String md5NewPass = HashPassword.getMD5(newPassword);
        String md5RepeatPass = HashPassword.getMD5(repeatPassword);

        validation(currentPasswordEdit);
        validation(newPasswordEdit);
        validation(repeatPasswordEdit);

        if (currentPasswordEdit.length() == 0 && newPasswordEdit.length() == 0 && repeatPasswordEdit.length() == 0) {
            currentPasswordEdit.setError("Enter Current Password");
            newPasswordEdit.setError("Enter New Password");
            repeatPasswordEdit.setError("Enter Repeat Password");
        }

        if (md5CurrentPass.equals(md5pass) && !(currentPassword.isEmpty() && md5pass.isEmpty()))
            if (md5NewPass.equals(md5RepeatPass)) {
                String md5pass = HashPassword.getMD5(newPassword);
                requestQueue = Volley.newRequestQueue(this);
                getChangePassword(email, newPassword, md5pass);
            }
            else {
                repeatPasswordEdit.setError("Repeat Password doesn't match");
            }
        else
            currentPasswordEdit.setError("Enter Valid Current Password");
    }

    public void cancel(View view){
        dialog.dismiss();
    }

    public void logout() {
        SweetAlertDialog sweetDialog = SweetDialogBoxHelper.showProgress(ClaimsOverviewActivity.this);
        UpdateServerFromLocallyStorage.updateSavedChanges(this, userId);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        SharedPreferencesHandler.removeClaimData(this, userId);
        SharedPreferencesHandler.removeNeedPasswordChange(this, email);
        SharedPreferencesHandler.clearLoginInfo(this, email, md5pass);

        sweetDialog.cancel();
        SweetDialogBoxHelper.showSuccess(ClaimsOverviewActivity.this, "Successfully Logout");
    }

    private void getChangePassword(String email, String newPassword, String hashPassword) {
        sweetAlertDialog = SweetDialogBoxHelper.showProgress(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL + "/methodPostChangePasswd",
            response -> endChangePassword(hashPassword),
            error -> endChangePassword(hashPassword)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("em", email);
                params.put("np", newPassword);
                params.put("ph", hashPassword);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void endChangePassword(String newPasswordHash) {
        SharedPreferencesHandler.setNeedPasswordChange(this, email);
        SharedPreferencesHandler.replaceLoginInfo(this, userId, md5pass, newPasswordHash);
        md5pass = newPasswordHash;
        sweetAlertDialog.cancel();
        dialog.dismiss();
        SweetDialogBoxHelper.showSuccess(ClaimsOverviewActivity.this, "Password Changed");
    }

    private void setClaims(String jsonClaims) {
        Claim[] claims = parseClaims(jsonClaims);
        finishSetClaims(claims);
    }

    private void offlineSetClaims() {
        Claim[] claims = SharedPreferencesHandler.getClaims(getApplicationContext(), userId);
        finishSetClaims(claims);
    }

    private void finishSetClaims(Claim[] claims) {
        adapter = new ClaimsListAdapter(this, claims);
        recyclerView.setAdapter(adapter);
        sweetAlertDialog.cancel();
    }

    private Claim[] parseClaims(String jsonClaims) {
        ClaimResponse claimResponse = GSON.fromJson(jsonClaims, ClaimResponse.class);
        int numberOfClaims = Integer.parseInt(claimResponse.numberOfClaims) + 1;
        Context context = getApplicationContext();

        Claim[] claims = new Claim[numberOfClaims];
        claims[0] = new Claim();
        for (int i = 1; i < numberOfClaims; i++) {
            int index = i - 1;
            Claim claim = new Claim();
            claim.id = claimResponse.claimId[index];
            claim.description = claimResponse.claimDes[index];
            claim.location = claimResponse.claimLocation[index];
            claim.photo = claimResponse.claimPhoto[index];
            claim.status = claimResponse.claimStatus[index];
            claims[i] = claim;

            SharedPreferencesHandler.updateClaim(context, claim, userId);
        }
        return claims;
    }

    public void launchClaimActivity(Claim claim) {
        Intent intent = new Intent(this, ClaimDetailActivity.class);
        intent.putExtra(CLAIM, GSON.toJson(claim, Claim.class));
        intent.putExtra(USER_ID, userId);
        startActivity(intent);
    }

    public void launchAddClaimActivity() {
        if (adapter.getItemCount() > 5) {
            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Not allowed to have more then 5 claims")
                    .show();
            return;
        }
        Intent intent = new Intent(this, ClaimDetailActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(NUMBER_OF_CLAIMS, String.valueOf(adapter.getItemCount() - 1));
        startActivity(intent);
    }

    public void validation(EditText text) {
        if (text.length() == 0)
            text.setError("Enter required data");
    }
}