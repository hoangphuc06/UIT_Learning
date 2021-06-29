package com.example.uit_learning.model;

import java.util.List;

public class Post {
    String pId;
    String pTitle;
    String pDescr;
    String pLikes;
    String pImage;
    String pTime;
    String uid;
    String uEmail;
    String pComments;

    public Post() {
    }

    public Post(String pId, String pTitle, String pDescr, String pLikes, String pImage, String pTime, String uid, String uEmail, String pComments) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pLikes = pLikes;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.pComments = pComments;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

}
