package com.example.uit_learning.model;

import java.util.List;

public class Course {
    private String title;
    private String type;
    private String id;
    private String image;
    private List<String> content;

    public Course(String title, List<String> content,String type,String id, String image) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.id = id;
        this.image = image;
    }

    public Course() {
        title = "";
        content = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
