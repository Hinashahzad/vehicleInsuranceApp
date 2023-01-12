package com.example.vehicleinsurance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReadClaimFragment extends ClaimParentFragment {
    private static final String URL = "http://10.0.2.2:8080";
    private static final String LOG_TAG = ReadClaimFragment.class.getSimpleName();
    private ImageView imageView;
    private String imageFolder;

    public static ReadClaimFragment newInstance(String claimString) {
        ReadClaimFragment fragment = new ReadClaimFragment();
        Bundle args = new Bundle();
        args.putString(CLAIM, claimString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_read_claim, container, false);
        TextView statusText = view.findViewById(R.id.status_text);
        statusText.setText(claim.status);

        TextView descriptionText = view.findViewById(R.id.description_text);
        descriptionText.setText(claim.description);

        Button locationButton = view.findViewById(R.id.location_button);
        if (claim.location == null || claim.location.equals("na")) {
            locationButton.setText(R.string.location_not_set);
            locationButton.setEnabled(false);
        } else {
            locationButton.setText(claim.location);
            locationButton.setOnClickListener(click -> onOpenMapClick());
        }


        imageView = view.findViewById(R.id.claim_image);
        if (claim.photo == null || claim.photo.equals("na"))
            return view;

        imageFolder = getActivity().getApplicationContext().getFilesDir() + File.separator + "VehicleInsurance" + File.separator + "Images";
        File imageFile = new File(imageFolder, claim.photo);
        if (!imageFile.exists()) {
            downloadImage(claim.photo);
            return view;
        }

        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);
        return view;
    }

    private void onOpenMapClick() {
        addEditListener.onOpenMapClick(claim.location, false);
    }

    private void downloadImage(String fileName) {
        String downloadImageUrl = URL + "/getMethodDownloadPhoto?fileName=" + fileName;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                downloadImageUrl,
                response -> setImage(response, fileName),
                error -> Log.d(LOG_TAG, "Something went wrong")
        );
        queue.add(stringRequest);
    }

    private void setImage(String response, String fileName) {
        byte[] decodedImage = Base64.decode(response, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        imageView.setImageBitmap(bitmap);
        File outFile = new File(imageFolder, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}