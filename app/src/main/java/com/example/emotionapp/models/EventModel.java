package com.example.emotionapp.models;

public class EventModel {
    String name,date,month;
    int image1,image2;

    public EventModel(String name, String date, String month, int image1, int image2) {
        this.name = name;
        this.date = date;
        this.month = month;
        this.image1 = image1;
        this.image2 = image2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getImage1() {
        return image1;
    }

    public void setImage1(int image1) {
        this.image1 = image1;
    }

    public int getImage2() {
        return image2;
    }

    public void setImage2(int image2) {
        this.image2 = image2;
    }
}
