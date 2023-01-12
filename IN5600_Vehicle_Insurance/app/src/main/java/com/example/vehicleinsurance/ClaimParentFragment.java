package com.example.vehicleinsurance;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

public class ClaimParentFragment extends Fragment {
    protected Gson gson = new Gson();
    protected static final String CLAIM = "CLAIM";
    protected IAddEditListener addEditListener;

    protected String claimString;
    protected Claim claim = new Claim();

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
        claimString = getArguments().getString(CLAIM);
        if (claimString != null)
            claim = gson.fromJson(claimString, Claim.class);
    }
}
