package com.example.emotionapp.models;

public enum Mood {
    Happy(0), Smile(1), Neutral(2), Sad(3), Angry(4);

    private final int value;

    Mood(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
