package com.example.sapacoordinator;

import android.annotation.SuppressLint;
import android.content.Intent;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapacoordinator.Connector.ApiClient;
import com.example.sapacoordinator.Connector.ApiInterface;
import com.example.sapacoordinator.Connector.GenericResponse;
import com.example.sapacoordinator.Connector.ServerDetector;

public class
SignUpActivity extends AppCompatActivity {
    EditText firstnameInput, lastnameInput, emailInput, passwordInput;
    Button registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firstnameInput = findViewById(R.id.firstname);
        lastnameInput = findViewById(R.id.lastname);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        registerBtn = findViewById(R.id.sign_up_button);
        registerBtn.setOnClickListener(v -> register());


        TextView sign_up_direct = findViewById(R.id.sign_up_direct);
        sign_up_direct.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

    }
    private void register() {
        String firstname = firstnameInput.getText().toString().trim();
        String lastname = lastnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Missing Fields")
                    .setContentText("Please fill in all fields.")
                    .show();
            return;
        }

        // Detect server first
        ServerDetector.detectServer(SignUpActivity.this, new ServerDetector.OnServerFoundListener() {
            @Override
            public void onServerFound(String baseUrl) {
                ApiClient.setBaseUrl(baseUrl);
                sendRegisterRequest(firstname, lastname, email, password);
            }

            @Override
            public void onServerNotFound() {
                showError("Server not found. Check your connection.");
            }

            @Override
            public void onDetectionError(Exception e) {
                showError("Error detecting server: " + e.getMessage());
            }
        });
    }

    private void sendRegisterRequest(String firstname, String lastname, String email, String password) {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = api.registerUser(firstname, lastname, email, password);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleRegisterResponse(response.body());
                } else {
                    showError("Server error: " + response.code());
                }
                resetFields();
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                showError("Connection Error: " + t.getMessage());
            }
        });
    }


    private void handleRegisterResponse(GenericResponse res) {
        if (res.isSuccess()) {
            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Registration Successful!")
                    .setContentText(res.getMessage())
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        } else {
            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Registration Failed")
                    .setContentText(res.getMessage())
                    .show();
        }
    }

    private void showError(String message) {
        new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Connection Error")
                .setContentText(message)
                .show();
    }


    @SuppressLint("SetTextI18n")
    private void resetFields() {
        firstnameInput.setText("");
        lastnameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
    }


}


