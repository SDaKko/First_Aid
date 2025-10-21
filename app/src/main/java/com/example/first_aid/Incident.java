package com.example.first_aid;

public class Incident {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private int viewId; // Добавляем поле для ID View

    public Incident() {}

    public Incident(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Incident(int id, String title, String description, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getViewId() { return viewId; }
    public void setViewId(int viewId) { this.viewId = viewId; }
}