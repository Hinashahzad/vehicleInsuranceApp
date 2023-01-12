package com.example.vehicleinsurance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_ID = "com.example.vehicleinsurance.extra.USER_ID";
    public static final String PASSWORD = "com.example.vehicleinsurance.extra.PASSWORD";
    public static final String EMAIL = "com.example.vehicleinsurance.extra.EMAIL";
    private static final String LOGIN_SUCCESSFULLY = "Login Successfully";
    private final Gson GSON = new Gson();
    private final String URL = "http://10.0.2.2:8080";
    private EditText userEmail, userPassword;
    private String email, md5pass;
    private RequestQueue requestQueue;
    private SweetAlertDialog sweetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userEmail = findViewById(R.id.edit_email);
        userPassword = findViewById(R.id.edit_password);
    }

    public void logIn(View view) {
        validation(userEmail);
        validation(userPassword);
        email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        md5pass = HashPassword.getMD5(password);
        if (ValidateEmail.isValidEmail(email))
            if (!localLogin(password))
                volleyRequestMethod();
        else
            userEmail.setError("Invalid Email Address");
    }

    private boolean localLogin(String clearPassword) {
        String userId = SharedPreferencesHandler.localLogin(this, email, md5pass);
        if (userId.isEmpty())
            return false;

        boolean passwordChanged = SharedPreferencesHandler.getNeedPasswordChange(this, email);
        if (passwordChanged)
            updatePassword(userId, clearPassword);

        successfulLogin(userId);
        return true;
    }

    private void updatePassword(String userId, String clearPassword) {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL + "/methodPostChangePasswd",
                response -> UpdateServerFromLocallyStorage.updateSavedChanges(this, userId),
                error -> successfulLogin(userId)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("em", email);
                params.put("np", clearPassword);
                params.put("ph", md5pass);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void volleyRequestMethod() {
        sweetDialog = SweetDialogBoxHelper.showProgress(LoginActivity.this);
        requestQueue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin",
                response -> serverOnlineLoginMethod(response),
                failed -> onServerFail(failed)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("em", email);
                params.put("ph", md5pass);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void onServerFail(VolleyError volleyError) {
        volleyError.printStackTrace();
        if (volleyError.networkResponse == null || volleyError.getMessage() == null) {
            serverOfflineLoginMethod();
        }
    }

    private void serverOnlineLoginMethod(String response) {
        sweetDialog.cancel();
        if (!(response.isEmpty())) {
            Person person = GSON.fromJson(response, Person.class);
            SharedPreferencesHandler.writeLoginInfo(getApplicationContext(), person.id, person.email, person.passHash);
            UpdateServerFromLocallyStorage.updateSavedChanges(this, person.id);
            successfulLogin(person.id);
        }
        else
            loginFailed();
        requestQueue.stop();
    }

    private void serverOfflineLoginMethod() {
        String userId = SharedPreferencesHandler.localLogin(this, email, md5pass);
        sweetDialog.cancel();
        if (!(userId.isEmpty()))
            successfulLogin(userId);
        else
            loginFailed();
    }

    private void successfulLogin(String userId) {
        SweetDialogBoxHelper.showSuccess(LoginActivity.this, LOGIN_SUCCESSFULLY);
        activeUser(userId, email, md5pass);
    }

    private void loginFailed() {
        SweetDialogBoxHelper.showError(LoginActivity.this, "Login Failed");
        userEmail.getText().clear();
        userPassword.getText().clear();
    }

    public void activeUser(String userId, String email, String md5pass) {
        Intent intent = new Intent(this, ClaimsOverviewActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(PASSWORD, md5pass);
        intent.putExtra(EMAIL, email);
        startActivity(intent);
    }

    private void validation(EditText text) {
        if (text.length() == 0)
            text.setError("Enter required data");
    }
}


