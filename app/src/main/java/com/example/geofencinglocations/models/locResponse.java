package com.example.geofencinglocations.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class locResponse {

    @Expose
    @SerializedName("userId")
    String userId;

    @Expose
    @SerializedName("placeLatitude")
    String placeLatitude;

    @Expose
    @SerializedName("placeLongitude")
    String placeLongitude;

    @Expose
    @SerializedName("placeAddress")
    String placeAddress;

    @Expose
    @SerializedName("placeName")
    String placeName;

    @Expose
    @SerializedName("placeType")
    String placeType;

    @Expose
    @SerializedName("visitStatus")
    String visitStatus;

    @Expose
    @SerializedName("placeTime")
    String placeTime;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public String getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(String visitStatus) {
        this.visitStatus = visitStatus;
    }

    public String getPlaceTime() {
        return placeTime;
    }

    public void setPlaceTime(String placeTime) {
        this.placeTime = placeTime;
    }
}
