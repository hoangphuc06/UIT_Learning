package com.example.uit_learning.model;

public class Unit {
    String filename;
    String fileurl;
    String id;

    public Unit() {

    }

    public Unit(String filename, String fileurl, String id) {
        this.filename = filename;
        this.fileurl = fileurl;
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
