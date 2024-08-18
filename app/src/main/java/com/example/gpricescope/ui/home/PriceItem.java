package com.example.gpricescope.ui.home;

import java.util.HashMap;

public class PriceItem {


    private final String code;
    private final String value;
    private final String currency;
    private final static Countries country = new Countries();

    public PriceItem(String code, String value, String currency) {
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

    public String getValue() {
        return value;
    }

    public String getPrice() {
        return value + " " + currency;
    }
}
