package com.example.first_aid;

public class Incident {
    private String title;
    private String description;
    private int viewId;

    public Incident(String title, String description, int viewId) {
        this.title = title;
        this.description = description;
        this.viewId = viewId;
    }

    public Incident(String title, String description) {
        this.title = title;
        this.description = description;
        this.viewId = -1; // или 0
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}