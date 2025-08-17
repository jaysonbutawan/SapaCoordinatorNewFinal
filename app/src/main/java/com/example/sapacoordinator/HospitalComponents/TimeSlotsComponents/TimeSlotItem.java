package com.example.sapacoordinator.HospitalComponents.TimeSlotsComponents;

import com.google.gson.annotations.SerializedName;

public class TimeSlotItem {
    @SerializedName("time_slot_id")
    private int time_slot_id;
    @SerializedName("date_slot_id")
    private int date_slot_id;
    @SerializedName("start_time")
    private String start_time;
    @SerializedName("end_time")
    private String end_time;
    @SerializedName("capacity")
    private int capacity;

    public TimeSlotItem(int time_slot_id, int date_slot_id, String start_time, String end_time, int capacity) {
        this.time_slot_id = time_slot_id;
        this.date_slot_id = date_slot_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.capacity = capacity;
    }

    public int getTime_slot_id() {
        return time_slot_id;
    }

    public int getDate_slot_id() {
        return date_slot_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public int getCapacity() {
        return capacity;
    }
}
