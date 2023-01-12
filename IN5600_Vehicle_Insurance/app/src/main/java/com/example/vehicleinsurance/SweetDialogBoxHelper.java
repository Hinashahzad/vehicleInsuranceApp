package com.example.vehicleinsurance;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialogBoxHelper {
    private static final String LOADING = "Loading...";
    public static SweetAlertDialog showProgress(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(LOADING);
        pDialog.setCancelable(true);
        pDialog.show();
        return pDialog;
    }

    public static void showSuccess(Context context, String message){
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(message)
                .show();
    }

    public static void showError(Context context, String message){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(message)
                .show();
    }

    public static void showWarning(Context context, String message){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(message)
                .show();
    }

    public static void showNormal(Context context, String message){
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(message)
                .show();
    }
}
