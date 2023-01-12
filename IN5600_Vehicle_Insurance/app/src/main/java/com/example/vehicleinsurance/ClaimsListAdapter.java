package com.example.vehicleinsurance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClaimsListAdapter extends RecyclerView.Adapter<ClaimsListAdapter.ViewHolder> {
    private Claim[] claims;
    private ClaimsOverviewActivity claimsOverviewActivity;

    public ClaimsListAdapter(ClaimsOverviewActivity claimsOverviewActivity, Claim[] claims) {
        this.claims = claims;
        this.claimsOverviewActivity = claimsOverviewActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.claim_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Claim claim = claims[position];
        if (claim.id != null) {
            holder.textView.setText(claim.id);
            holder.linearLayout.setOnClickListener(
                    onClick -> claimsOverviewActivity.launchClaimActivity(claim)
            );
        }
        else
        {
            holder.textView.setText("+");
            holder.linearLayout.setOnClickListener(
                    onClick -> claimsOverviewActivity.launchAddClaimActivity()
            );
        }
    }

    @Override
    public int getItemCount() {
        return claims.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.id_textView);
            linearLayout = itemView.findViewById(R.id.row_Layout);
        }
    }
}