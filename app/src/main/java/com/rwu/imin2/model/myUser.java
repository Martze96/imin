package com.rwu.imin2.model;

public class myUser {

    private String displayName;
    private String email;
    private String userID;
    private String password;

    public myUser(String displayName, String email, String userID, String password) {
        this.displayName = displayName;
        this.email = email;
        this.userID = userID;
        this.password = password;
    }

    public myUser(String displayName, String email, String userID) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.userID = userID;
    }

    public myUser(String email, String password) {
        this.email = email;
        this.password = password;

    }

    public myUser() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
