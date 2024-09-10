package com.assignment1.meetingmanagement;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Meeting implements Serializable {
    private String meetingName;
    private String date;
    private String time;

    private String contactName;

    private String phoneNumber;

    // Constructor, takes input for 6 of the attributes from the App.
    public Meeting(String meetingName, String date, String time, String contactName, String phoneNumber){
        this.meetingName = meetingName;
        this.date = date;
        this.time = time;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    // to string
    @NonNull
    @Override
    public String toString() {
        return "Meeting: " + meetingName + ", Date: " + date + ", Time: " + time;
    }

    // getter setters
    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
