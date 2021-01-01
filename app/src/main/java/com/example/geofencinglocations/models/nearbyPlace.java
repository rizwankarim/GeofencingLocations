package com.example.geofencinglocations.models;

import java.util.List;

public class nearbyPlace {
    private String placeName;
    private double placeLatitude;
    private double placeLongitude;
    private List<String> types;
    private String mainPlaceAddress;

    public nearbyPlace(String placeName, double placeLatitude, double placeLongitude, List<String> types) {
        this.placeName = placeName;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.types = types;
    }

    public nearbyPlace(String placeName, double placeLatitude, double placeLongitude) {
        this.placeName = placeName;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
    }

    public nearbyPlace(String placeName, double placeLatitude, double placeLongitude, List<String> types, String mainPlaceAddress) {
        this.placeName = placeName;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.types = types;
        this.mainPlaceAddress = mainPlaceAddress;
    }

    public List<String> getTypes() { return types; }

    public void setTypes(List<String> types) { this.types = types; }

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

    public String getMainPlaceAddress() { return mainPlaceAddress; }

    public void setMainPlaceAddress(String mainPlaceAddress) { this.mainPlaceAddress = mainPlaceAddress; }
}

