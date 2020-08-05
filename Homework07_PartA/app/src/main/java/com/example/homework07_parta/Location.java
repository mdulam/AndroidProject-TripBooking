package com.example.homework07_parta;

public class Location {
    String latitude,longitude, place, photoUrl;

    public Location(){ }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Location(String latitude, String longitude, String place, String photoUrl) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place=place;
        this.photoUrl=photoUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
