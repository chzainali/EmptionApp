package com.example.emotionapp.notifications;

import java.io.Serializable;

public class ScheduleModel implements Serializable {
    String details;
    long time;

    public ScheduleModel(String details, long time) {
        this.details = details;
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
