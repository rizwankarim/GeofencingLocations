package com.example.geofencinglocations.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    @Expose
    private com.example.jobscheduler.nearbyResponse.Location location;

    /**
     * @return The location
     */
    public com.example.jobscheduler.nearbyResponse.Location getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(com.example.jobscheduler.nearbyResponse.Location location) {
        this.location = location;
    }
}