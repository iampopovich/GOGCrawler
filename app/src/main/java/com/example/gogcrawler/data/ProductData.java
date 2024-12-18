package com.example.gogcrawler.data;

public class ProductData {
    private final String id;
    private final String title;

    public ProductData(String id, String title) {
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
