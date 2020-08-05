package com.example.homework07_parta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trip implements Serializable {

    String latitude,longitude, place, photoUrl,createdBy, title;
    String tripId;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Trip(String latitude, String longitude, String place, String photoUrl, String createdBy, String title, List<String> users, String tripId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.photoUrl = photoUrl;
        this.createdBy = createdBy;
        this.title = title;
        this.users=users;
        this.tripId=tripId;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Trip(Map tripMap){
        this.setTitle((String) tripMap.get("Title"));
        this.setPlace((String) tripMap.get("Place"));
        this.setLatitude((String) tripMap.get("Latitude"));
        this.setLongitude((String) tripMap.get("Longitude"));
        this.setPhotoUrl((String) tripMap.get("PhotoUrl"));
        this.setCreatedBy((String) tripMap.get("CreatedBy"));
        this.setUsers((List<String>) tripMap.get("users"));
        this.setTripId((String) tripMap.get("tripId"));
    }
    public Map toUserMap(){
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("Title",this.title);
        objectMap.put("Place",this.place);
        objectMap.put("Latitude",this.latitude);
        objectMap.put("Longitude",this.longitude);
        objectMap.put("PhotoUrl",this.photoUrl);
        objectMap.put("CreatedBy",this.createdBy);
        objectMap.put("users", this.users);
        objectMap.put("tripId", this.tripId);
        return objectMap;
    }

}
