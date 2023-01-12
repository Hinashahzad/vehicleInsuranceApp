package com.example.vehicleinsurance;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private String latitude;
    private String longitude;
    private boolean isEditable;

    private GoogleMap mMap;
    private Marker marker;
    private IAddEditListener addEditListener;

    public static MapsFragment newInstance(String latLng, boolean isEditable) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        if (latLng != null && !latLng.equals("na"))
            args.putString("latLng", latLng);
        args.putBoolean("isEditable", isEditable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddEditListener)
            addEditListener = (IAddEditListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null)
            return;
        String latLng = getArguments().getString("latLng");
        isEditable = getArguments().getBoolean("isEditable");
        if (latLng != null) {
            latitude = latLng.split(",")[0];
            longitude = latLng.split(",")[1];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_maps, container, false);

        if (isEditable) {
            LinearLayout editLayout = view.findViewById(R.id.edit_buttons);
            editLayout.setVisibility(View.VISIBLE);
            Button selectButton = view.findViewById(R.id.btn_selectLocation);
            selectButton.setOnClickListener(click -> selectLocation());
        } else {
            LinearLayout editLayout = view.findViewById(R.id.read_buttons);
            editLayout.setVisibility(View.VISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("Marker", "Started");
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        System.out.println("Longitude and latitude " + marker.getPosition().latitude + " ," + marker.getPosition().longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng;
        if (latitude != null && longitude != null)
            latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        else
            latLng = new LatLng(59.9435475643, 10.7183767855);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.0f));
        if (isEditable) {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
            mMap.setOnMarkerDragListener(this);
        } else {
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    public void selectLocation() {
        SweetDialogBoxHelper.showSuccess(getContext(), "Location Selected");
        addEditListener.reopenEdit(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
    }
}
