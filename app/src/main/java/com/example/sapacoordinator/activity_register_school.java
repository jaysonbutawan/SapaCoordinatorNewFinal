package com.example.sapacoordinator;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activity_register_school extends AppCompatActivity {
    EditText etSchoolName,etAddress,etContactInfo;
    Button btn_register_school;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_school);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_school), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         etSchoolName = findViewById(R.id.etSchoolName);
         etAddress = findViewById(R.id.etAddress);
         etContactInfo = findViewById(R.id.etContactInfo);
        btn_register_school = findViewById(R.id.btnRegister);
        btn_register_school.setOnClickListener(v -> {
            String schoolName = etSchoolName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String contactInfo = etContactInfo.getText().toString().trim();
//            int userId = /* retrieve from logged-in session or intent */;

//            if (schoolName.isEmpty() || address.isEmpty()) {
//                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Missing Fields")
//                        .setContentText("Please fill all required fields.")
//                        .show();
//            } else {
//                addSchool(schoolName, address, contactInfo, userId);
//            }
        });
    }
    private void addSchool(String school_name, String school_address, String contact_info, int user_id) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = api.registerSchool(school_name, school_address, contact_info, user_id);

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Debug raw API JSON
                    Log.d("API_RESPONSE_RAW", new Gson().toJson(response.body()));

                    GenericResponse res = response.body();
                    if (res.isSuccess()) {
                        new SweetAlertDialog(activity_register_school.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText(res.getMessage())
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                    // Optionally clear fields or go back
                                    etSchoolName.setText("");
                                    etAddress.setText("");
                                    etContactInfo.setText("");
                                })
                                .show();
                    } else {
                        new SweetAlertDialog(activity_register_school.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Failed")
                                .setContentText(res.getMessage())
                                .show();
                    }
                } else {
                    Log.e("API_ERROR", "Server error: Code " + response.code());
                    new SweetAlertDialog(activity_register_school.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Server Error")
                            .setContentText("Code: " + response.code())
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                new SweetAlertDialog(activity_register_school.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Connection Error")
                        .setContentText(t.getMessage())
                        .show();
            }
        });
    }

}