package com.example.gpricescope.ui.home;

public class PriceItem {


    private final String country;
    private final String price;

    public PriceItem(String country, String price) {
        this.country = country;
        this.price = price;
    }

    public String getCountry() {
        return country;
    }

    public String getPrice() {
        return price;
    }
}
