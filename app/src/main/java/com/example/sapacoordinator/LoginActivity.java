package com.example.sapacoordinator;
import com.example.sapacoordinator.R;
import com.google.gson.Gson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText login_email_input = findViewById(R.id.login_email_input);
        EditText login_password_input = findViewById(R.id.login_password_input);
        Button login_button = findViewById(R.id.login_button);

        TextView sign_in_direct = findViewById(R.id.sign_in_direct);
        sign_in_direct.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        login_button.setOnClickListener(v -> {
            String email = login_email_input.getText().toString().trim();
            String password = login_password_input.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Missing Fields")
                        .setContentText("Please fill in both Email and Password.")
                        .show();
            } else {
                loginUser(email, password);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void loginUser(String email, String password) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = api.loginUser(email, password);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Log raw JSON (debugging)
                    Log.d("API_RESPONSE_RAW", new Gson().toJson(response.body()));

                    GenericResponse res = response.body();
                    if (res.isSuccess()) {
                        int userId = res.getUserId(); // You'll add getter in GenericResponse

                        // Save to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                        prefs.edit().putInt("user_id", userId).apply();
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Welcome!")
                                .setContentText(res.getMessage())
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Login Failed")
                                .setContentText(res.getMessage())
                                .show();
                    }
                } else {
                    Log.e("API_ERROR", "Server error: Code " + response.code());
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Server Error")
                            .setContentText("Code: " + response.code())
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Connection Error")
                        .setContentText(t.getMessage())
                        .show();
            }
        });

    }

}