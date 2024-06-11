package com.example.emotionapp.auth.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id,fName,lName,email,imageUri;
    Long coins;
    public  User(){

    }

    public User(String id, String fName, String lName, String email, String imageUri, Long coins) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.imageUri = imageUri;
        this.coins = coins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }
}
