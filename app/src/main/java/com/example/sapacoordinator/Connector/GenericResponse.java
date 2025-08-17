package com.example.sapacoordinator.Connector;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    private String status;
    private String message;
    @SerializedName("student_count")
    private int studentCount;
    private int user_id;

    // Check if API returned success
    public boolean isSuccess() {
        return status != null &&
                (status.equalsIgnoreCase("success") ||
                        status.equalsIgnoreCase("ok") ||
                        status.equalsIgnoreCase("true"));
    }

    public String getMessage() {
        return message;
    }



    public int getStudent_count() { return studentCount; }

    public int getUserId() {
        return user_id;
    }
}
