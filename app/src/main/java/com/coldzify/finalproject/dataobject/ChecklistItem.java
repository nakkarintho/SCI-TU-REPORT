package com.coldzify.finalproject.dataobject;



import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ChecklistItem {
    private String itemName,type,id;
    private boolean check;
    private Timestamp timestamp;
    public ChecklistItem(){}
    public ChecklistItem(String itemName,String type){
        this.itemName = itemName;
        this.type = type;
        this.check = false;
    }
    public ChecklistItem(String itemName){
        this.itemName = itemName;
        type = "";
        this.check = false;
    }

    public String getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getItemName() {
        return itemName;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    public String getType() {
        return type;
    }
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("itemName",itemName);
        map.put("type",type);
        //map.put("timestamp", FieldValue.serverTimestamp());

        return  map;
    }
}
