package com.example.uit_learning.model;

public class Comment {
    String pId, cId, comment, timestamp, uid, uEmail;

    public Comment() {
    }

    public Comment(String pId, String cId, String comment, String timestamp, String uid, String uEmail) {
        this.pId = pId;
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.uEmail = uEmail;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

}
