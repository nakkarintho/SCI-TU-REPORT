package com.coldzify.finalproject.dataobject;


import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Checklist {
    private String housekeeper,listName,id;
    private Timestamp timestamp;

    public Checklist(){}
    public Checklist( String listName,String housekeeper) {
        this.housekeeper = housekeeper;
        this.listName = listName;
    }

    public String getHousekeeper() {
        return housekeeper;
    }

    public String getListName() {
        return listName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("listName",listName);
        map.put("housekeeper",housekeeper);
        //map.put("timestamp", FieldValue.serverTimestamp());

        return  map;
    }
}
