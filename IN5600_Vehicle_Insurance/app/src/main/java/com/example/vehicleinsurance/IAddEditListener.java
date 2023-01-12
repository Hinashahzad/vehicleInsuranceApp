package com.example.vehicleinsurance;

import android.net.Uri;

public interface IAddEditListener {
    void onAddClick(Claim claim);
    void onSaveClick(Claim claim);
    void reopenEdit(String lat, String lng);
    void onOpenMapClick(String latLng, boolean isEditable);
    String imageChanged(Uri newImageUri, String fileName);
}
