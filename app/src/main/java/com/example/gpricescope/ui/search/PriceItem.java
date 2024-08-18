package com.example.gpricescope.ui.search;

public class PriceItem {


    private final String code;
    private final Double value;
    private final String currency;
    private final static Countries country = new Countries();

    public PriceItem(String code, Double value, String currency) {
        this.code = code;
        this.value = value;
        this.currency = currency;
    }

    public String getCountry() {
        return country.getCountry(code);
    }

    public String getCurrency() {
        return currency;
    }

    public Double getValue() {
        return value;
    }

    public String getPrice() {
        return String.format("%.2f", value) + " " + currency;
    }
}
