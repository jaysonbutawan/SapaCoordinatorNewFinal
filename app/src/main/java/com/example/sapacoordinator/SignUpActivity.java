package com.example.sapacoordinator;

import android.annotation.SuppressLint;
import android.content.Intent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        String firstname = firstnameInput.getText().toString();
        String lastname = lastnameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = api.registerUser(firstname, lastname, email, password);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    Toast.makeText(SignUpActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    if (res.isSuccess() ||
                            (res.getMessage() != null && res.getMessage().toLowerCase().contains("already have an account"))) {

                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                resetFields();
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, "Connection Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void resetFields() {
        firstnameInput.setText("");
        lastnameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
    }

}


