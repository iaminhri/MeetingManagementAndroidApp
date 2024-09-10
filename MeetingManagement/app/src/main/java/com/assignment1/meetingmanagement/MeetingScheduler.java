package com.assignment1.meetingmanagement;

import java.util.ArrayList;
import java.util.List;

public class MeetingScheduler {
    private List<Meeting> meetings;

    public MeetingScheduler(){
        meetings = new ArrayList<>();
    }

    // add meetings to the list.
    public Meeting addMeeting(String meetingName, String date, String time, String contactName, String phoneNumber){
        Meeting meeting = new Meeting(meetingName, date, time, contactName, phoneNumber);
        meetings.add(meeting);
        return meeting;
    }

    public List<Meeting> getMeetingsByDate(String date){
        List<Meeting> meetingsByDate = new ArrayList<>();
        for(Meeting meeting : meetings){
            if(meeting.getDate().equals(date)){
                meetingsByDate.add(meeting);
            }
        }

        return meetingsByDate;
    }

    public List<Meeting> fetchAllMeetings(){
        return meetings;
    }
}
