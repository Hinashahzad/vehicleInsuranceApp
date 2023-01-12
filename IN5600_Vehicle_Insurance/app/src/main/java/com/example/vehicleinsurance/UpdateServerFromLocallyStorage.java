package com.example.vehicleinsurance;

import android.content.Context;

import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UpdateServerFromLocallyStorage {
    public static void updateSavedChanges(Context context, String userId) {
        String[] addClaimIds = SharedPreferencesHandler.getNeedServerAdd(context, userId);
        String[] updateClaimIds = SharedPreferencesHandler.getNeedServerUpdate(context, userId);
        String[] imageFileNames = SharedPreferencesHandler.getNeedUploadImage(context, userId);
        SharedPreferencesHandler.removeNeedServerUpdate(context, userId);
        ServerCommunicationInterface serverCommunicationInterface = new ServerCommunicationInterface(Volley.newRequestQueue(context), userId);
        if (imageFileNames.length > 0) {
            String imageFolder = context.getFilesDir() + File.separator + "VehicleInsurance" + File.separator + "Images";
            for (String imageFileName : imageFileNames) {
                File imageFile = new File(imageFolder, imageFileName);
                if(!imageFile.exists())
                    continue;
                try {
                    byte[] imageByteArray = Files.readAllBytes(imageFile.toPath());
                    serverCommunicationInterface.uploadImage(imageByteArray, imageFileName,
                            success -> {},
                            error -> SharedPreferencesHandler.setNeedUploadImage(context, userId, imageFileName)
                    );
                }
                catch (IOException ignored) {}
            }
        }
        if (addClaimIds.length > 0) {
            for (String claimId : addClaimIds) {
                Claim claim = SharedPreferencesHandler.getClaim(context, userId, claimId);
                serverCommunicationInterface.addNewClaim(claim,
                        success -> {},
                        failed -> SharedPreferencesHandler.setNeedServerAdd(context, userId, claimId));
            }
        }

        if (updateClaimIds.length > 0) {
            for (String claimId : updateClaimIds) {
                Claim claim = SharedPreferencesHandler.getClaim(context, userId, claimId);
                serverCommunicationInterface.updateClaim(claim,
                        success -> {},
                        failed -> SharedPreferencesHandler.setNeedServerUpdate(context, userId, claimId));
            }
        }
    }
}
