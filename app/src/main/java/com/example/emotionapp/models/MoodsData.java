package com.example.emotionapp.models;

import android.graphics.drawable.Drawable;

public class MoodsData {
    Drawable icon;
    String percent,color;

    public MoodsData(Drawable icon, String percent, String color) {
        this.icon = icon;
        this.percent = percent;
        this.color = color;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
