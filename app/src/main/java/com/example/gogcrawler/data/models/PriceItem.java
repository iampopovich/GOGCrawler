package com.example.gogcrawler.data.models;

import java.text.DecimalFormat;

public class PriceItem {
    private final String code;
    private final Double value;

    public PriceItem(String code, Double value) {
        this.code = code;
        this.value = value;
    }

    public String getCountry() {
        return Countries.codes.get(code);
    }

    public Double getValue() {
        return value;
    }

    public String getPrice() {
        return new DecimalFormat("#.## USD").format(value);
    }
}
