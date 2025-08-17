package com.example.sapacoordinator.HospitalComponents.TimeSlotsComponents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapacoordinator.R;

public class DateTimeSlotSelectionActivity extends AppCompatActivity
        implements DateSlotList.OnDateSelectedListener, TimeSlotList.OnTimeSlotSelectedListener {

    private int departmentId;
    private int schoolId = -1; // You'll need to pass this from previous activity
    private int selectedDateSlotId = -1;
    private int selectedTimeSlotId = -1;
    private Button btnBookAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_slot_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.date_timeselection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        departmentId = getIntent().getIntExtra("department_id", -1);
        schoolId = getIntent().getIntExtra("school_id", -1); // Get school_id from intent

        // Initialize button
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnBookAppointment.setEnabled(false); // Disabled until both date and time are selected

        btnBookAppointment.setOnClickListener(v -> {
            if (isBookingDataValid()) {
                proceedToStudentSelection();
            } else {
                Toast.makeText(this, "Please select both date and time slot", Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            DateSlotList dateSlotListFragment = DateSlotList.newInstance(departmentId);
            dateSlotListFragment.setCallback(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dateSlotContainer, dateSlotListFragment)
                    .commit();
        }
    }

    @Override
    public void onDateSelected(int dateSlotId) {
        Log.d("DateTimeSlotSelection", "Selected dateSlotId: " + dateSlotId);
        selectedDateSlotId = dateSlotId;
        // Reset time slot selection when date changes
        selectedTimeSlotId = -1;
        updateBookButtonState();

        TimeSlotList existingFragment = (TimeSlotList) getSupportFragmentManager()
                .findFragmentById(R.id.timeSlotContainer);

        if (existingFragment != null) {
            existingFragment.updateTimeSlots(dateSlotId);
        } else {
            TimeSlotList timeslotList = TimeSlotList.newInstance(dateSlotId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.timeSlotContainer, timeslotList)
                    .commit();
        }
    }

    @Override
    public void onTimeSlotSelected(int timeSlotId) {
        Log.d("DateTimeSlotSelection", "Selected timeSlotId: " + timeSlotId);
        selectedTimeSlotId = timeSlotId;
        updateBookButtonState();
    }

    private boolean isBookingDataValid() {
        // Add debug logging to see what values we have
        Log.d("BookingValidation", "Checking booking data:");
        Log.d("BookingValidation", "departmentId: " + departmentId);
        Log.d("BookingValidation", "schoolId: " + schoolId);
        Log.d("BookingValidation", "selectedDateSlotId: " + selectedDateSlotId);
        Log.d("BookingValidation", "selectedTimeSlotId: " + selectedTimeSlotId);

        boolean isValid = departmentId != -1 &&
                schoolId != -1 &&
                selectedDateSlotId != -1 &&
                selectedTimeSlotId != -1;

        Log.d("BookingValidation", "Is valid: " + isValid);
        return isValid;
    }

    private void updateBookButtonState() {
        btnBookAppointment.setEnabled(selectedDateSlotId != -1 && selectedTimeSlotId != -1);
    }

    private void proceedToStudentSelection() {
        Intent intent = new Intent(this, SelectStudentActivity.class);
        intent.putExtra("school_id", schoolId);
        intent.putExtra("department_id", departmentId);
        intent.putExtra("date_slot_id", selectedDateSlotId);  // Fixed: using "date_slot_id"
        intent.putExtra("time_slot_id", selectedTimeSlotId);

        Log.d("BookingData", "Proceeding with: school_id=" + schoolId +
                ", department_id=" + departmentId +
                ", date_slot_id=" + selectedDateSlotId +
                ", time_slot_id=" + selectedTimeSlotId);

        startActivity(intent);
    }
}
