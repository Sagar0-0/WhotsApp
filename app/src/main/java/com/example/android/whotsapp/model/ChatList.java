package com.example.android.whotsapp.model;

public class ChatList {
    private String userId;
    private String userName;
    private String description;
    private String date;
    private String urlProfile;

    public ChatList() {
    }

    public ChatList(String userId, String userName, String description, String date, String urlProfile) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.urlProfile = urlProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
