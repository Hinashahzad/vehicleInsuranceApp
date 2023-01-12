package com.example.vehicleinsurance;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddEditClaimFragment extends ClaimParentFragment {
    private static final int SELECT_PICTURE = 1;
    private EditText descriptionText, statusText;
    private Button locationButton, imageButton;
    private Uri newImageUri;
    private boolean imageChange = false;

    public static AddEditClaimFragment newInstance(String claimString) {
        AddEditClaimFragment fragment = new AddEditClaimFragment();
        if (claimString == null)
            return fragment;

        Bundle args = new Bundle();
        args.putString(CLAIM, claimString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_claim, container, false);
        descriptionText = view.findViewById(R.id.edit_description);
        statusText = view.findViewById(R.id.edit_status);
        Button saveButton = view.findViewById(R.id.save_button);

        locationButton = view.findViewById(R.id.location_button);
        locationButton.setOnClickListener(click -> onOpenMapClick());

        imageButton = view.findViewById(R.id.image_button);
        imageButton.setOnClickListener(click -> onImageButtonClick());
        if (claim.photo == null || claim.photo.equals("na"))
            imageButton.setText(R.string.add_image);
        else
            imageButton.setText(R.string.change_image);

        if (claim.location != null && !claim.location.equals("na")) {
            locationButton.setText(claim.location);
        }

        if (claim.id != null) {
            statusText.setText(claim.status);
            descriptionText.setText(claim.description);

            saveButton.setOnClickListener(click -> onSaveClick());
            saveButton.setText(R.string.save);
        }
        else
        {
            saveButton.setOnClickListener(click -> onAddClick());
            saveButton.setText(R.string.add);
        }
        return view;
    }

    private void onAddClick() {
        updateClaim();
        if (imageChange)
            claim.photo = addEditListener.imageChanged(newImageUri, "");
        addEditListener.onAddClick(claim);
    }

    private void onSaveClick() {
        updateClaim();
        if (imageChange)
            claim.photo = addEditListener.imageChanged(newImageUri, claim.id);
        addEditListener.onSaveClick(claim);
    }

    private void updateClaim() {
        claim.description = descriptionText.getText().toString();
        claim.status = statusText.getText().toString();
    }

    public void onImageButtonClick() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , SELECT_PICTURE);
    }

    public void setLocationData(String latitude, String longitude) {
        String location = latitude + "," + longitude;
        claim.location = location;
        locationButton.setText(location);
    }

    public void onOpenMapClick() {
        addEditListener.onOpenMapClick(claim.location, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != -1)
            return;
        if (requestCode == SELECT_PICTURE) {
            imageChange = true;
            newImageUri = data.getData();
            imageButton.setText(R.string.change_image);
            SweetDialogBoxHelper.showSuccess(getContext(), "Image added");
        }
    }
}