package com.example.android.whotsapp.model;

public class User {
    private String userId;
    private String userName;
    private String userPhone;
    private String imageProfile;
    private String description;
    private String lastDescriptionUpdate;

    public User() {
    }

    public User(String userId, String userName, String userPhone, String imageProfile, String description, String lastDescriptionUpdate) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.description = description;
        this.lastDescriptionUpdate = lastDescriptionUpdate;
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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastDescriptionUpdate() {
        return lastDescriptionUpdate;
    }

    public void setLastDescriptionUpdate(String lastDescriptionUpdate) {
        this.lastDescriptionUpdate = lastDescriptionUpdate;
    }
}
