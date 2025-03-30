package com.example.gogcrawler.data.models;

public class ProductModel {
    private final String id;
    private final String title;

    public ProductModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
