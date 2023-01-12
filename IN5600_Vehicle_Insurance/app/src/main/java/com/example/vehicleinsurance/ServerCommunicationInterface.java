package com.example.vehicleinsurance;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ServerCommunicationInterface {
    private final static String URL = "http://10.0.2.2:8080";
    private final RequestQueue queue;
    private final String userId;
    public ServerCommunicationInterface(RequestQueue queue, String userId) {
        this.queue = queue;
        this.userId = userId;
    }

    public void getClaims(Response.Listener<String> success, Response.ErrorListener failure) {
        String getClaimUrlParams = URL + "/getMethodMyClaims?id=" + userId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                getClaimUrlParams,
                success,
                failure
        );
        queue.add(stringRequest);
    }

    public void addNewClaim(Claim newClaim, Response.Listener<String> success, Response.ErrorListener failure) {
        String addClaimUrl = URL + "/postInsertNewClaim";
        Claim claim = setNullValues(newClaim);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, addClaimUrl, success, failure)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("indexUpdateClaim", claim.id);
                map.put("newClaimDes", claim.description);
                map.put("newClaimLoc", claim.location);
                map.put("newClaimPho", claim.photo);
                map.put("newClaimSta", claim.status);
                return map;
            }
        };
        queue.add(stringRequest);
    }

    public void updateClaim(Claim claimToUpdate, Response.Listener<String> success, Response.ErrorListener failure) {
        String updateClaimUrl = URL + "/postUpdateClaim";
        Claim claim = setNullValues(claimToUpdate);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateClaimUrl, success, failure)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("indexUpdateClaim", claim.id);
                map.put("updateClaimDes", claim.description);
                map.put("updateClaimLoc", claim.location);
                map.put("updateClaimPho", claim.photo);
                map.put("updateClaimSta", claim.status);
                return map;
            }
        };
        queue.add(stringRequest);
    }

    public void uploadImage(byte[] imageByteArray, String fileName, Response.Listener<String> success, Response.ErrorListener failure) {
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
        String uploadImageUrl = URL + "/postMethodUploadPhoto";
        String claimId = fileName.split("_")[1];
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                uploadImageUrl,
                success,
                failure
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("claimId", claimId);
                map.put("fileName", fileName);
                map.put("imageStringBase64", encodedImage);
                return map;
            }
        };
        queue.add(stringRequest);
    }

    private static Claim setNullValues(Claim claim) {
        if (claim.photo == null)
            claim.photo = "na";
        if (claim.location == null)
            claim.location = "na";
        if (claim.description == null)
            claim.photo = "na";
        if (claim.status == null)
            claim.location = "na";
        return claim;
    }
}
