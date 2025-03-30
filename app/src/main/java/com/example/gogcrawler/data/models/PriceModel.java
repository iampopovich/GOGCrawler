package com.example.gogcrawler.data.models;

public class PriceModel {
    private final String code;
    private final Double value;

    public PriceModel(String code, Double value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public Double getValue() {
        return value;
    }

    public String getCountry() {
        return Countries.codes.get(code);
    }

    public String getFormattedPrice() {
        return String.format("%.2f USD", value);
    }
}
