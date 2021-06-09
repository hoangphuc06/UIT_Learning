package com.example.uit_learning.model;

import java.util.List;

public class Course {
    private String title;
    private List<String> content;

    public Course(String title, List<String> content) {
        this.title = title;
        this.content = content;
    }

    public Course() {
        title = "";
        content = null;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getContent() {
        return content;
    }
}
