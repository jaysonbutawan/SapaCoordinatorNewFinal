package com.example.sapacoordinator.HospitalComponents.TimeSlotsComponents;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapacoordinator.R;

public class DateTimeSlotSelectionActivity extends AppCompatActivity
        implements DateSlotList.OnDateSelectedListener {  // FIX: implement the fragment interface

    private int departmentId;

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

        if (savedInstanceState == null) {
            DateSlotList dateSlotListFragment = DateSlotList.newInstance(departmentId);
            dateSlotListFragment.setCallback(this); // ✅ pass activity as listener
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dateSlotContainer, dateSlotListFragment)
                    .commit();
        }
    }

    @Override
    public void onDateSelected(int dateSlotId) {
        Log.d("DateTimeSlotSelection", "Selected dateSlotId: " + dateSlotId);

        TimeSlotList existingFragment = (TimeSlotList) getSupportFragmentManager()
                .findFragmentById(R.id.timeSlotContainer);

        if (existingFragment != null) {
            // If already added, just update the list
            existingFragment.updateTimeSlots(dateSlotId);
        } else {
            // First time load
            TimeSlotList timeslotList = TimeSlotList.newInstance(dateSlotId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.timeSlotContainer, timeslotList)
                    .commit();
        }
    }
}
