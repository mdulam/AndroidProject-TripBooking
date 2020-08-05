package com.example.homework07_parta;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
    String msg,url,name,id,uid;
    Date time;
    String tripId;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Message(String msg, String url, String name, String id, String uid, Date time, String tripId) {
        this.msg = msg;
        this.url = url;
        this.name = name;
        this.id = id;
        this.uid = uid;
        this.time = time;
        this.tripId=tripId;
    }

    public Message() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {return uid;}
    public void setUid(String uid) { this.uid = uid; }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message(Map messageMap){
        this.setMsg((String) messageMap.get("Message"));
        this.setUrl((String) messageMap.get("Url"));
        this.setName((String) messageMap.get("Name"));
        this.setId((String) messageMap.get("Id"));
        this.setUid((String) messageMap.get("UId"));
        this.setTime(((Timestamp) messageMap.get("Date")).toDate());
        this.setTripId((String) messageMap.get("tripId"));
    }
    public Map toUserMap(){
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("Message",this.msg);
        objectMap.put("Url",this.url);
        objectMap.put("Name",this.name);
        objectMap.put("Id",this.id);
        objectMap.put("UId",this.uid);
        objectMap.put("Date",this.time);
        objectMap.put("tripId", this.tripId);
        return objectMap;
    }

}
