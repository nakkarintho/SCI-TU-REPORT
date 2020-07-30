package com.coldzify.finalproject.dataobject;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Notifications {
    private String message,title,type, topic,commenter,reportID;
    private Timestamp timestamp;

    public Notifications(){}
    public Notifications(String message, String title, String type, String reportID,String topic) {
        this.message = message;
        this.title = title;
        this.type = type;
        this.topic = topic;
        this.reportID = reportID;
        this.timestamp = new Timestamp(new Date());
    }
    public Notifications(String message, String title, String type, String topic,String reportID, String commenter) {
        this.message = message;
        this.title = title;
        this.type = type;
        this.topic = topic;
        this.reportID = reportID;
        this.commenter = commenter;

        this.timestamp = new Timestamp(new Date());
    }

    public String getReportID() {
        return reportID;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getTopic() {
        return topic;
    }

    public String getCommenter() {
        return commenter;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
