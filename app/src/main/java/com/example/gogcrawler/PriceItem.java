package com.example.gogcrawler;

import java.text.DecimalFormat;

public class PriceItem {


    private final static Countries country = new Countries();
    private final String code;
    private final Double value;

    public PriceItem(String code, Double value) {
        this.code = code;
        this.value = value;
    }

    public String getCountry() {
        return country.getCountry(code);
    }

    public Double getValue() {
        return value;
    }

    public String getPrice() {
        return new DecimalFormat("#.## USD").format(value);
    }
}
