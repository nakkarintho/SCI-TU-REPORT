package com.coldzify.finalproject.dataobject;

import android.support.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;



import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report implements Comparable<Report>{
    private List<String> pictures;
    private String type;
    private String detail,room;
    private int placeCode;
    private String creatorID;
    private Timestamp timestamp;
    private int status;
    private GeoPoint geoPoint;
    private String reportID;
    public static final int STATUS_WAITING = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_IN_PROGRESS = 3;
    public static final int STATUS_FINISHED = 4;
    public Report(){}
    public Report(List<String> pictures, String type, String detail,GeoPoint geoPoint, int placeCode, String room, String creatorID) {
        this.pictures = pictures;
        this.type = type;
        this.detail = detail;
        this.placeCode = placeCode;
        this.room = room;
        this.creatorID = creatorID;


        this.status = STATUS_WAITING;
        this.geoPoint = geoPoint;

        //DateFormat dateFormat = DateFormat.getDateInstance().format()
    }

    public Report(List<String> pictures, String type, String detail, int placeCode, String room , int status , String creatorID, Timestamp timestamp) {
        this.pictures = pictures;
        this.type = type;
        this.detail = detail;
        this.placeCode = placeCode;
        this.creatorID = creatorID;
        this.timestamp = timestamp;
        this.status = status;
        this.room = room;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public String getRoom(){return room;}
    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public int  getPlaceCode() {
        return placeCode;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getCreatorID() {
        return creatorID;
    }
    public Timestamp getTimestamp(){
        return timestamp;
    }

    public String getReportID() {
        return reportID;
    }


    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("creatorID",creatorID);
        map.put("detail",detail);
        map.put("pictures",pictures);
        map.put("placeCode",placeCode);
        map.put("room",room);
        map.put("status",status);
        map.put("type",type);
        map.put("geoPoint",geoPoint);
        //map.put("timestamp", FieldValue.serverTimestamp());

        return  map;
    }


    @Override
    public int compareTo(@NonNull Report report) {
        return timestamp.compareTo(report.getTimestamp());
    }
    public static String statusCodeToString(int code){
        switch (code){
            case STATUS_WAITING:
                return "รอพิจารณา";

            case STATUS_ACCEPTED :
                return "พิจารณาแล้ว";

            case STATUS_IN_PROGRESS :
                return "กำลังดำเนินการ";

            case STATUS_FINISHED :
                return "เสร็จสิ้น";
                default: return "";
        }
    }
}
