package com.example.yttest.Model;

public class MarkerData {
    private String lat;
    private String lon;
    private String registeredUserEmail;
    private String objectID;

    public MarkerData(String lat, String lon, String registeredUserEmail) {
        this.lat = lat;
        this.lon = lon;
        this.registeredUserEmail = registeredUserEmail;
    }

    public MarkerData(){}

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getRegisteredUserEmail() {
        return registeredUserEmail;
    }

    public void setRegisteredUserEmail(String registeredUserEmail) {
        this.registeredUserEmail = registeredUserEmail;
    }
}
