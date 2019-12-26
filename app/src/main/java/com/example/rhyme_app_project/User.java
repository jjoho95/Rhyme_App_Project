package com.example.rhyme_app_project;


public class User {
    private String userID;
    private String PW;
    private String CompID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPW() {
        return PW;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

    public String getCompID() {
        return CompID;
    }

    public void setCompID(String CompID) {
        this.CompID = CompID;
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", PW=" + PW + ", CompID="
                + CompID + "]";
    }
}

