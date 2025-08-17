package com.example.sapacoordinator.HospitalComponents.TimeSlotsComponents;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapacoordinator.R;

public class SelectStudentActivity extends AppCompatActivity {

    private int schoolId;
    private int departmentId;
    private int dateSlotId;
    private int timeSlotId;

    private TextView tvTrainingInfo;
    private TextView tvSlotCount;
    private TextView tvRemainingSlots;
    private TextView tvSelectedStudentsCount;
    private RecyclerView rvAvailableStudent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_students_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.select_student), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get booking data from intent
        schoolId = getIntent().getIntExtra("school_id", -1);
        departmentId = getIntent().getIntExtra("department_id", -1);
        dateSlotId = getIntent().getIntExtra("date_slot_id", -1);
        timeSlotId = getIntent().getIntExtra("time_slot_id", -1);

        // Log the received data
        Log.d("DEBUG_", "Received booking data:");
        Log.d("DEBUG_", "School ID: " + schoolId);
        Log.d("DEBUG_", "Department ID: " + departmentId);
        Log.d("DEBUG_", "Date Slot ID: " + dateSlotId);
        Log.d("DEBUG_", "Time Slot ID: " + timeSlotId);

        // Initialize views
        initializeViews();

        // Validate booking data
        if (!isBookingDataValid()) {
            Log.e("DEBUG_", "Invalid booking data received");
            finish(); // Close activity if data is invalid
            return;
        }

        // Load students and time slot capacity
        loadTimeSlotCapacity();
        loadAvailableStudents();
    }

    private void initializeViews() {
        // Initialize TextViews (you may need to adjust IDs based on your actual layout)
        rvAvailableStudent = findViewById(R.id.rvAvailableStudent);

        // If you have these TextViews in your layout, uncomment and adjust IDs:
        // tvTrainingInfo = findViewById(R.id.tvTrainingInfo);
        // tvSlotCount = findViewById(R.id.tvSlotCount);
        // tvRemainingSlots = findViewById(R.id.tvRemainingSlots);
        // tvSelectedStudentsCount = findViewById(R.id.tvSelectedStudentsCount);
    }

    private boolean isBookingDataValid() {
        return schoolId != -1 && departmentId != -1 && dateSlotId != -1 && timeSlotId != -1;
    }

    private void loadTimeSlotCapacity() {
        // TODO: Implement API call to get time slot capacity
        // This will help determine how many students can be selected
        Log.d("DEBUG_", "Loading time slot capacity for time slot ID: " + timeSlotId);
    }

    private void loadAvailableStudents() {
        // TODO: Implement API call to load available students for the school
        Log.d("DEBUG_", "Loading available students for school ID: " + schoolId);
    }

    // TODO: Add methods for:
    // - Student selection/deselection
    // - Final booking submission
    // - UI updates for selected students count
}