package com.example.geofencinglocations.models;

//import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class place {

    private int placeId;

    private String userId;

    private double placeLatitude;

    private double placeLongitude;

    @SerializedName("formatted_address")
    private String placeAddress;

    @SerializedName("name")
    private String placeName;

    @SerializedName("types")
    private ArrayList<String> placeType;

    @SerializedName("visitStatus")
    private String status;

    @SerializedName("placeTime")
    private String placeTime;

    public place(int placeId, String userId, double placeLatitude, double placeLongitude, String placeAddress, String placeName,
                 ArrayList<String> placeType, String visitStatus, String placeTime) {
        this.placeId=placeId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.placeAddress = placeAddress;
        this.placeName = placeName;
        this.placeType = placeType;
        this.userId=userId;
        this.status=visitStatus;
        this.placeTime=placeTime;
    }

    public place(String userId, double placeLatitude, double placeLongitude, String placeAddress, String visitStatus, String placeTime) {
        this.userId=userId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.placeAddress = placeAddress;
        this.status=visitStatus;
        this.placeTime=placeTime;
    }

    public place() { }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public ArrayList<String> getPlaceType() { return placeType; }

    public void setPlaceType(ArrayList<String> placeType) { this.placeType = placeType; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public int getPlaceId() { return placeId; }

    public void setPlaceId(int placeId) { this.placeId = placeId; }

    public String getPlaceTime() { return placeTime; }

    public void setPlaceTime(String placeTime) { this.placeTime = placeTime; }

    @Override
    public String toString() {
        return "place{" +
                ", userId='" + userId + '\'' +
                ", placeLatitude=" + placeLatitude +
                ", placeLongitude=" + placeLongitude +
                ", placeAddress='" + placeAddress + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeType=" + placeType +
                ", status='" + status + '\'' +
                ", placeTime='" + placeTime + '\'' +
                '}';
    }
}
