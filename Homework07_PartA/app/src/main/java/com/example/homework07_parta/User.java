package com.example.homework07_parta;

import java.util.HashMap;
import java.util.Map;

public class User {
    String firstName;
    String lastName;
    String photoURL;
    String gender;

    public User(String firstName, String lastName, String photoURL, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoURL = photoURL;
        this.gender = gender;
    }

    public User(){

    }
    public User(Map userMap){
        this.setFirstName((String) userMap.get("Firstname"));
        this.setLastName((String) userMap.get("Lastname"));
        this.setGender((String) userMap.get("Gender"));
        this.setPhotoURL((String) userMap.get("Image"));
    }
    public Map toUserMap(){
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("Firstname",this.firstName);
        objectMap.put("Lastname",this.lastName);
        objectMap.put("Image",this.photoURL);
        objectMap.put("Gender",this.gender);
        return objectMap;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
