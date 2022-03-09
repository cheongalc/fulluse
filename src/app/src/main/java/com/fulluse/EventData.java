package com.fulluse;

/**
 * Created by Al Cheong on 6/11/2017.
 */

public class EventData {
    private String eventName;
    private String eventTruncatedName;
    private String eventStartTiming;
    private String eventEndTiming;
    private String eventStartDate;
    private String eventEndDate;
    private boolean isCompleted;

    public EventData(String eventName, String eventStartTiming, String eventEndTiming, String eventStartDate, String eventEndDate, boolean isCompleted) {
        this.eventName = eventName;
        this.eventTruncatedName = eventName;
        this.eventStartTiming = eventStartTiming;
        this.eventEndTiming = eventEndTiming;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.isCompleted = isCompleted;
    }

    public String getEventName() {return eventName;}
    public String getEventTruncatedName() {return eventTruncatedName;}
    public String getEventStartTiming() {return eventStartTiming;}
    public String getEventEndTiming() {return eventEndTiming;}
    public String getEventStartDate() {return eventStartDate;}
    public String getEventEndDate() {return eventEndDate;}
    public boolean getIsCompleted() {return isCompleted;}

    public void setEventName(String newName) {this.eventName = newName;}
    public void setEventTruncatedName(String newName) {this.eventTruncatedName = newName;}
    public void setEventTiming(String newStart, String newEnd) {
        this.eventStartTiming = newStart;
        this.eventEndTiming = newEnd;
    }
    public void setEventDates(String newStartDate, String newEndDate) {
        this.eventStartDate = newStartDate;
        this.eventEndDate = newEndDate;
    }
    public void setEventCompletedState(boolean newState) {this.isCompleted = newState;}
}
