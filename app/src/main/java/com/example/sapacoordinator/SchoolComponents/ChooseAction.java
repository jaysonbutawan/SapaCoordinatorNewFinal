package com.example.sapacoordinator.SchoolComponents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapacoordinator.Connector.ApiClient;
import com.example.sapacoordinator.Connector.ApiInterface;
import com.example.sapacoordinator.Connector.GenericResponse;
import com.example.sapacoordinator.HospitalComponents.HospitalActivity;
import com.example.sapacoordinator.R;
import com.example.sapacoordinator.SchoolComponents.StudentsComponents.StudentActivity;
import com.example.sapacoordinator.SchoolComponents.StudentsComponents.StudentsRegistration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChooseAction extends AppCompatActivity {

    private String schoolName, schoolAddress, schoolContact;
    private int schoolId;
    private int userId;
    private TextView tvStudentsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_action);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.choose_action), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get current logged-in user_id from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Get school data from Intent
        Intent receivedIntent = getIntent();
        schoolName = receivedIntent.getStringExtra("school_name");
        schoolAddress = receivedIntent.getStringExtra("school_address");
        schoolContact = receivedIntent.getStringExtra("school_contact");
        schoolId = receivedIntent.getIntExtra("school_id", -1);

        // Bind TextViews
        TextView tvSchoolName = findViewById(R.id.tvSchoolName);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvContact = findViewById(R.id.tvContact);
        tvStudentsCount = findViewById(R.id.tvStudentsCount);

        // Display school data
        if (schoolName != null) tvSchoolName.setText(schoolName);
        if (schoolAddress != null) tvAddress.setText(schoolAddress);
        if (schoolContact != null) tvContact.setText(schoolContact);

        // Handle Add Student Button
        Button btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAction.this, StudentsRegistration.class);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("school_name", schoolName);
            intent.putExtra("school_address", schoolAddress);
            intent.putExtra("school_contact", schoolContact);
            startActivity(intent);
        });

        Button bookAppointment = findViewById(R.id.btnBookAppointment);
        bookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAction.this, HospitalActivity.class);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });


        // Handle Students Count click
        LinearLayout llStudentsCount = findViewById(R.id.studentsCountContainer);
        llStudentsCount.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAction.this, StudentActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("school_id", schoolId);
            startActivity(intent);
        });

        fetchStudentCount();
    }

    private void fetchStudentCount() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = apiInterface.getStudentCount(userId, schoolId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvStudentsCount.setText(String.valueOf(response.body().getStudent_count()));
                } else {
                    tvStudentsCount.setText("0");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                tvStudentsCount.setText("0");
                Toast.makeText(ChooseAction.this, "Failed to load count", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

