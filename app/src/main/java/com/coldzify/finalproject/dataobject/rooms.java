package com.coldzify.finalproject.dataobject;


import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class rooms {
    private String housekeeper_id,name;

    public rooms(){}
    public rooms(String housekeeper_id, String name) {
        this.housekeeper_id = housekeeper_id;
        this.name = name;
    }

    public String gethousekeeper_id() {
        return housekeeper_id;
    }

    public String getname() {
        return name;
    }


}
