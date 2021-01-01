package com.example.geofencinglocations.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {
    @SerializedName("results")
    private List<place> results;

    @SerializedName("status")
    private String status;

    @SerializedName("candidates")
    private List<place> candidates;

    public List<place> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<place> candidates) {
        this.candidates = candidates;
    }

    public List<place> getResults() {
        return results;
    }

    public void setResults(List<place> results) {
        this.results = results;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
