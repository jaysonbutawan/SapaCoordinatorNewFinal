package com.example.sapacoordinator.HospitalComponents.TimeSlotsComponents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapacoordinator.Connector.ApiClient;
import com.example.sapacoordinator.Connector.ApiInterface;
import com.example.sapacoordinator.Connector.GenericResponse;
import com.example.sapacoordinator.R;
import com.example.sapacoordinator.SchoolComponents.StudentsComponents.Student;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectStudentActivity extends AppCompatActivity implements BookingStudentAdapter.OnStudentSelectionListener {

    private int schoolId;
    private int departmentId;
    private int dateSlotId;
    private int timeSlotId;

    private TextView tvSlotCount;
    private TextView tvRemainingSlots;
    private TextView tvSelectedStudentsCount;
    private TextView tvSelectionSummary;
    private RecyclerView rvAvailableStudent;
    private Button btnContinueBooking;

    private BookingStudentAdapter adapter;
    private List<Student> studentList = new ArrayList<>();
    private int maxCapacity = 10; // Default capacity
    private int selectedCount = 0;

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
            finish();
            return;
        }

        // Initialize RecyclerView
        setupRecyclerView();

        // Load data
        loadTimeSlotCapacity();
        loadAvailableStudents();
    }

    private void initializeViews() {
        rvAvailableStudent = findViewById(R.id.rvAvailableStudent);
        btnContinueBooking = findViewById(R.id.btnContinueBooking);

        // Initialize TextViews with actual IDs from your layout
        tvSlotCount = findViewById(R.id.tvSlotCount);
        tvRemainingSlots = findViewById(R.id.tvRemainingSlots);
        tvSelectedStudentsCount = findViewById(R.id.tvSelectedStudentsCount);
        tvSelectionSummary = findViewById(R.id.tvSelectionSummary);

        // Handle continue button click
        btnContinueBooking.setOnClickListener(v -> {
            if (selectedCount > 0) {
                proceedToFinalBooking();
            } else {
                Toast.makeText(this, "Please select at least one student", Toast.LENGTH_SHORT).show();
            }
        });

        updateUI();
    }

    private void setupRecyclerView() {
        adapter = new BookingStudentAdapter(this, studentList, this);
        rvAvailableStudent.setLayoutManager(new LinearLayoutManager(this));
        rvAvailableStudent.setAdapter(adapter);
    }

    private boolean isBookingDataValid() {
        return schoolId != -1 && departmentId != -1 && dateSlotId != -1 && timeSlotId != -1;
    }

    private void loadTimeSlotCapacity() {
        // TODO: Replace with actual API call to get time slot capacity
        // For now, using default capacity
        Log.d("DEBUG_", "Loading time slot capacity for time slot ID: " + timeSlotId);
        maxCapacity = 10; // This should come from API
        adapter.setMaxSelections(maxCapacity);
        updateUI();
    }

    private void loadAvailableStudents() {
        Log.d("DEBUG_", "Loading available students for school ID: " + schoolId);

        SharedPreferences prefs = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Student>> call = api.getStudents(userId, schoolId);

        call.enqueue(new Callback<List<Student>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentList.clear();
                    studentList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    Log.d("DEBUG_", "Students loaded: " + studentList.size());
                    Log.d("API_RESPONSE", new Gson().toJson(response.body()));

                    updateUI();
                } else {
                    Log.e("DEBUG_", "Failed to load students: " + response.code());
                    Toast.makeText(SelectStudentActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                Log.e("DEBUG_", "Error loading students", t);
                Toast.makeText(SelectStudentActivity.this, "Error loading students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStudentSelected(Student student, boolean isSelected) {
        selectedCount = adapter.getSelectedCount();
        Log.d("DEBUG_", "Student " + student.getFirstname() + " " +
              (isSelected ? "selected" : "deselected") + ". Total selected: " + selectedCount);
        updateUI();
    }

    @Override
    public void onSelectionLimitReached(int maxLimit) {
        Toast.makeText(this, "Maximum " + maxLimit + " students can be selected", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        // Update slot count display
        if (tvSlotCount != null) {
            tvSlotCount.setText(selectedCount + "/" + maxCapacity);
        }

        // Update remaining slots
        int remaining = maxCapacity - selectedCount;
        if (tvRemainingSlots != null) {
            tvRemainingSlots.setText(remaining + " slots remaining");
        }

        // Update selected students count
        if (tvSelectedStudentsCount != null) {
            tvSelectedStudentsCount.setText("Selected Students (" + selectedCount + ")");
        }

        // Update selection summary
        if (tvSelectionSummary != null) {
            if (selectedCount == 0) {
                tvSelectionSummary.setText("No students selected");
            } else if (selectedCount == 1) {
                tvSelectionSummary.setText("1 Student Selected");
            } else {
                tvSelectionSummary.setText(selectedCount + " Students Selected");
            }
        }

        // Update continue button state
        if (btnContinueBooking != null) {
            btnContinueBooking.setEnabled(selectedCount > 0);
        }
    }

    private void proceedToFinalBooking() {
        Set<Integer> selectedStudentIds = adapter.getSelectedStudentIds();

        Log.d("DEBUG_", "Proceeding to final booking with:");
        Log.d("DEBUG_", "Selected students: " + selectedStudentIds.size());
        Log.d("DEBUG_", "Student IDs: " + selectedStudentIds.toString());

        // ✅ Show loading state
        btnContinueBooking.setEnabled(false);
        btnContinueBooking.setText("Submitting Booking...");

        // ✅ We need to get hospital_id from department data
        // For now, we'll use department_id as hospital_id (you may need to adjust this based on your data structure)
        int hospitalId = departmentId; // This might need to be adjusted based on your actual data relationship

        Log.d("DEBUG_", "Submitting booking with:");
        Log.d("DEBUG_", "  school_id: " + schoolId);
        Log.d("DEBUG_", "  hospital_id: " + hospitalId);
        Log.d("DEBUG_", "  time_slot_id: " + timeSlotId);
        Log.d("DEBUG_", "  department_id: " + departmentId);
        Log.d("DEBUG_", "  date_slot_id: " + dateSlotId);

        // ✅ Submit booking to API - call BookAppointment procedure for each student
        submitBookingForStudents(new ArrayList<>(selectedStudentIds), hospitalId);
    }

    private void submitBookingForStudents(List<Integer> studentIds, int hospitalId) {
        if (studentIds.isEmpty()) {
            // All students processed successfully
            showBookingSuccessDialog();
            return;
        }

        // Process one student at a time using the BookAppointment procedure
        int currentStudentId = studentIds.get(0);
        studentIds.remove(0); // Remove the current student from the list

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<GenericResponse> call = api.bookAppointment(
                schoolId,
                hospitalId,
                timeSlotId,
                currentStudentId
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();

                    if (genericResponse.isSuccess()) {
                        Log.e("DEBUG_", "Code: " + response.code());
                        Log.d("DEBUG_", "Student " + currentStudentId + " booked successfully");
                        // Continue with the next student
                        submitBookingForStudents(studentIds, hospitalId);
                    } else {
                        // Booking failed for this student
                        handleBookingError("Booking failed for student ID " + currentStudentId + ": " + genericResponse.getMessage());
                    }
                } else {
                    handleBookingError("API response failed: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                handleBookingError("Network error: " + t.getMessage());
            }
        });
    }

    private void handleBookingError(String errorMessage) {
        // Reset button state
        btnContinueBooking.setText("Continue to Final Booking");
        btnContinueBooking.setEnabled(true);

        Log.e("DEBUG_", errorMessage);
        Toast.makeText(SelectStudentActivity.this,
                "Booking failed: " + errorMessage,
                Toast.LENGTH_LONG).show();
    }

    // ✅ Show success dialog with booking details
    private void showBookingSuccessDialog() {
        // Reset button state
        btnContinueBooking.setText("Continue to Final Booking");
        btnContinueBooking.setEnabled(true);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Booking Successful! ✅")
                .setMessage("Your appointments have been submitted successfully!\n\n" +
                        "Students: " + selectedCount + " selected\n" +
                        "Status: Pending\n\n" +
                        "You will be notified when the hospital responds to your request.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // ✅ Navigate back to main screen
                    finish(); // This will close the SelectStudentActivity
                })
                .setCancelable(false)
                .show();
    }
}