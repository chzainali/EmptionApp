package com.example.emotionapp.models;

import com.example.emotionapp.activity.ImageModel;

import java.io.Serializable;
import java.util.HashMap;

public class EventsDto implements Serializable {
    String id, date, details, voice, userId;
    Long time;
    int mood;
    HashMap<String, ImageModel> images;

    public EventsDto(){

    }

    public EventsDto(String id, String date, String userId, String details, String voice, Long time, int mood, HashMap<String, ImageModel> images) {
        this.id = id;
        this.date = date;
        this.details = details;
        this.voice = voice;
        this.time = time;
        this.mood = mood;
        this.images = images;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public HashMap<String, ImageModel> getImages() {
        return images;
    }

    public void setImages(HashMap<String, ImageModel> images) {
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
