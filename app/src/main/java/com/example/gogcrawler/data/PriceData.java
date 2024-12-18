package com.example.gogcrawler.data;

public class PriceData {
    private final String countryCode;
    private final double price;
    private final String currency;

    public PriceData(String countryCode, double price, String currency) {
        this.countryCode = countryCode;
        this.price = price;
        this.currency = currency;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
