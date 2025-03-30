package com.example.gogcrawler.data.models;

import java.util.HashMap;
import java.util.Map;

public class Countries {
    public static final Map<String, String> codes = new HashMap<>();

    static {
        codes.put("US", "United States");
        codes.put("AR", "Argentina");
        codes.put("BS", "Bahamas");
        codes.put("BR", "Brazil");
        codes.put("CA", "Canada");
        codes.put("CL", "Chile");
        codes.put("CO", "Colombia");
        codes.put("CR", "Costa Rica");
        codes.put("GL", "Greenland");
        codes.put("MX", "Mexico");
        codes.put("PA", "Panama");
        codes.put("VE", "Venezuela");
        codes.put("AL", "Albania");
        codes.put("AD", "Andorra");
        codes.put("AT", "Austria");
        codes.put("BE", "Belgium");
        codes.put("BA", "Bosnia and Herzegovina");
        codes.put("BG", "Bulgaria");
        codes.put("HR", "Croatia");
        codes.put("CY", "Cyprus");
        codes.put("CZ", "Czech Republic");
        codes.put("DK", "Denmark");
        codes.put("EE", "Estonia");
        codes.put("FI", "Finland");
        codes.put("FR", "France");
        codes.put("DE", "Germany");
        codes.put("GR", "Greece");
        codes.put("HU", "Hungary");
        codes.put("IS", "Iceland");
        codes.put("IE", "Ireland");
        codes.put("IM", "Isle of Man");
        codes.put("IT", "Italy");
        codes.put("LV", "Latvia");
        codes.put("LI", "Liechtenstein");
        codes.put("LT", "Lithuania");
        codes.put("LU", "Luxembourg");
        codes.put("MT", "Malta");
        codes.put("MD", "Moldova");
        codes.put("MC", "Monaco");
        codes.put("ME", "Montenegro");
        codes.put("NL", "Netherlands");
        codes.put("MK", "North Macedonia");
        codes.put("NO", "Norway");
        codes.put("PL", "Poland");
        codes.put("PT", "Portugal");
        codes.put("RO", "Romania");
        codes.put("RS", "Serbia");
        codes.put("SK", "Slovakia");
        codes.put("SI", "Slovenia");
        codes.put("ES", "Spain");
        codes.put("SE", "Sweden");
        codes.put("CH", "Switzerland");
        codes.put("TR", "Turkey");
        codes.put("UA", "Ukraine");
        codes.put("GB", "United Kingdom");
        codes.put("AU", "Australia");
        codes.put("BD", "Bangladesh");
        codes.put("KH", "Cambodia");
        codes.put("CN", "China");
        codes.put("HK", "Hong Kong SAR China");
        codes.put("IN", "India");
        codes.put("ID", "Indonesia");
        codes.put("JP", "Japan");
        codes.put("MY", "Malaysia");
        codes.put("MN", "Mongolia");
        codes.put("NZ", "New Zealand");
        codes.put("PH", "Philippines");
        codes.put("SG", "Singapore");
        codes.put("LK", "Sri Lanka");
        codes.put("TW", "Taiwan");
        codes.put("VN", "Vietnam");
        codes.put("DZ", "Algeria");
        codes.put("AM", "Armenia");
        codes.put("EG", "Egypt");
        codes.put("GE", "Georgia");
        codes.put("IL", "Israel");
        codes.put("KZ", "Kazakhstan");
        codes.put("MA", "Morocco");
        codes.put("NG", "Nigeria");
        codes.put("QA", "Qatar");
        codes.put("SA", "Saudi Arabia");
        codes.put("ZA", "South Africa");
        codes.put("AE", "United Arab Emirates");
    }

    public static String getCountry(String code) {
        return codes.getOrDefault(code, "Unknown");
    }
}
