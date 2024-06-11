package com.example.emotionapp.activity;

import java.io.Serializable;

public class ImageModel implements Serializable {
    String id,url;

    public ImageModel(){

    }
    public ImageModel(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
