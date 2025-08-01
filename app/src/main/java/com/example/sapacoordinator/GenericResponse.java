package com.example.sapacoordinator;

public class GenericResponse {
    private String status;
    private String message;
    private String role;

    // Use int for DB boolean values
    private int is_verified;
    private int can_login;
    private int user_id;

    // Check if API returned success
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    // Convert DB int to boolean
    public boolean isVerified() {
        return is_verified == 1;
    }

    public boolean canLogin() {
        return can_login == 1;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public int getUserId() {
        return user_id;
    }
}
