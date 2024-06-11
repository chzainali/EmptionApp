package com.example.emotionapp.jsonreader;

import java.util.Map;

public class MotivationalQuotes {
    private Map<String, Category> motivational_quotes;

    public Map<String, Category> getMotivationalQuotes() {
        return motivational_quotes;
    }

    public MotivationalQuotes() {

    }

    public void setMotivationalQuotes(Map<String, Category> motivationalQuotes) {
        this.motivational_quotes = motivationalQuotes;
    }
}

