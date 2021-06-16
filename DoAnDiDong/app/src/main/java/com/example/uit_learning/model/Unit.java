package com.example.uit_learning.model;

public class Unit {
    String filename;
    String fileurl;
    String id;
    String idUnit;
    String typeUnit;


    public Unit() {

    }

    public Unit(String filename, String fileurl, String id, String idUnit, String typeUnit) {
        this.filename = filename;
        this.fileurl = fileurl;
        this.id = id;
        this.idUnit = idUnit;
        this.typeUnit = typeUnit;
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

    public String getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(String idUnit) {
        this.idUnit = idUnit;
    }

    public String getTypeUnit() {
        return typeUnit;
    }

    public void setTypeUnit(String typeUnit) {
        this.typeUnit = typeUnit;
    }
}
