package com.example.gogcrawler.repositories;

import com.android.volley.RequestQueue;
import com.example.gogcrawler.Countries;
import com.example.gogcrawler.data.GOGPriceSource;
import com.example.gogcrawler.data.PriceData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PriceRepository {
    private static final String TAG = "PriceRepository";
    private final GOGPriceSource priceSource;
    private final List<PriceData> prices = new ArrayList<>();
    private int pendingRequests = 0;

    public PriceRepository(RequestQueue requestQueue) {
        this.priceSource = new GOGPriceSource(requestQueue);
    }

    public void fetchPrices(String productId, OnPricesUpdatedListener listener) {
        prices.clear();
        pendingRequests = Countries.codes.size();

        for (String countryCode : Countries.codes.keySet()) {
            priceSource.fetchPrice(
                    productId,
                    countryCode,
                    response -> {
                        try {
                            JSONObject result = new JSONObject(response)
                                    .getJSONObject("_embedded")
                                    .getJSONArray("prices")
                                    .getJSONObject(0);

                            String[] priceParts = result.getString("finalPrice").split(" ");
                            double price = Integer.parseInt(priceParts[0]) / 100.00;
                            String currency = priceParts.length > 1 ? priceParts[1] : "USD";

                            synchronized (prices) {
                                prices.add(new PriceData(countryCode, price, currency));
                                pendingRequests--;

                                if (pendingRequests == 0) {
                                    listener.onPricesUpdated(new ArrayList<>(prices));
                                }
                            }
                        } catch (JSONException e) {
                            listener.onError("Error parsing price data: " + e.getMessage());
                        }
                    },
                    error -> {
                        synchronized (prices) {
                            pendingRequests--;
                            if (pendingRequests == 0) {
                                listener.onPricesUpdated(new ArrayList<>(prices));
                            }
                        }
                        listener.onError("Error fetching price: " + error.getMessage());
                    }
            );
        }
    }

    public void fetchPrices(String productId, OnPriceListener listener) {
        for (String countryCode : Countries.codes.keySet()) {
            priceSource.fetchPrice(
                    productId,
                    countryCode,
                    response -> {
                        try {
                            JSONObject result = new JSONObject(response)
                                    .getJSONObject("_embedded")
                                    .getJSONArray("prices")
                                    .getJSONObject(0);

                            String[] priceParts = result.getString("finalPrice").split(" ");
                            double price = Integer.parseInt(priceParts[0]) / 100.00;
                            String currency = priceParts.length > 1 ? priceParts[1] : "USD";

                            PriceData priceData = new PriceData(countryCode, price, currency);
                            listener.onPriceReceived(priceData);
                        } catch (JSONException e) {
                            listener.onError("Error parsing price data: " + e.getMessage());
                        }
                    },
                    error -> listener.onError("Error fetching price: " + error.getMessage())
            );
        }
    }

    public void cancelRequests() {
        priceSource.cancelRequests();
    }

    public interface OnPricesUpdatedListener {
        void onPricesUpdated(List<PriceData> prices);

        void onError(String error);
    }

    public interface OnPriceListener {
        void onPriceReceived(PriceData price);

        void onError(String error);
    }
}
