package com.example.sapacoordinator.HospitalComponents;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapacoordinator.Connector.ApiClient;
import com.example.sapacoordinator.Connector.ApiInterface;
import com.example.sapacoordinator.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalList extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmptyMessage;
    private HospitalAdapter adapter;
    private final List<Hospital> hospitalList = new ArrayList<>();

    public HospitalList() {

    }

    public static HospitalList newInstance() {
        return new HospitalList();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_list, container, false);

        recyclerView = view.findViewById(R.id.rvHospitals);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HospitalAdapter(hospitalList, requireContext());
        recyclerView.setAdapter(adapter);

        loadHospitals();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void loadHospitals() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            tvEmptyMessage.setText("User session expired. Please log in again.");
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Hospital>> call = api.getHospitals();

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Hospital>> call, @NonNull Response<List<Hospital>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hospitalList.clear();
                    hospitalList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (hospitalList.isEmpty()) {
                        tvEmptyMessage.setText("No hospitals found.");
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Hospital>> call, @NonNull Throwable t) {
                tvEmptyMessage.setText("Failed to load hospitals");
                Log.e("API_ERROR", "Load failed", t);
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}
