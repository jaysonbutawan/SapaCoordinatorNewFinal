package com.example.sapacoordinator;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Field;
public interface ApiInterface {

    // Register user
    @FormUrlEncoded
    @POST("android_api/UserRegistration.php")
    Call<GenericResponse> registerUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("password") String password
    );

    // Login user
    @FormUrlEncoded
    @POST("android_api/login.php")
    Call<GenericResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    // Add School
    @FormUrlEncoded
    @POST("register_school.php")
    Call<GenericResponse> registerSchool(
            @Field("school_name") String name,
            @Field("school_address") String address,
            @Field("contact_info") String contact,
            @Field("user_id") int user_id

    );

    //
}

