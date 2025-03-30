package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gogcrawler.data.models.PriceModel;
import com.example.gogcrawler.utils.Countries;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GetPricesUseCase {
    private final RequestQueue requestQueue;
    private static final String TAG = "GetPricesUseCase";
    private static final int MAX_PRICES_TO_SHOW = 10;
    private static final String DEFAULT_CURRENCY = "USD";
    private final List<PriceModel> prices = new ArrayList<>();
    private final AtomicInteger pendingRequests = new AtomicInteger(0);

    public GetPricesUseCase(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void execute(String productId, Consumer<List<PriceModel>> onSuccess, Consumer<String> onError) {
        prices.clear();
        pendingRequests.set(Countries.codes.size());
        
        for (String code : Countries.codes.keySet()) {
            String url = String.format("https://api.gog.com/products/%s/prices?countryCode=%s&currency=%s",
                    productId, code, DEFAULT_CURRENCY);
            Log.d(TAG, "Fetching price for " + Countries.getCountry(code) + " (" + code + "): " + url);
            
            StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject result = new JSONObject(response)
                                .getJSONObject("_embedded")
                                .getJSONArray("prices")
                                .getJSONObject(0);
                        
                        String finalPrice = result.getString("finalPrice");
                        String[] parts = finalPrice.split(" ");
                        double price = Double.parseDouble(parts[0]) / 100.0;
                        
                        Log.d(TAG, "Price received for " + Countries.getCountry(code) + ": " + price + " " + DEFAULT_CURRENCY);
                        
                        synchronized (prices) {
                            prices.add(new PriceModel(code, price));
                            if (pendingRequests.decrementAndGet() == 0) {
                                List<PriceModel> sortedPrices = prices.stream()
                                    .sorted(Comparator.comparingDouble(PriceModel::getValue))
                                    .limit(MAX_PRICES_TO_SHOW)
                                    .collect(Collectors.toList());
                                onSuccess.accept(sortedPrices);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Error parsing price for " + Countries.getCountry(code) + ": " + e.getMessage());
                        handlePendingRequest(onSuccess);
                    }
                },
                error -> {
                    Log.d(TAG, "Error fetching price for " + Countries.getCountry(code) + ": " + error.toString());
                    handlePendingRequest(onSuccess);
                }
            );
            request.setTag(TAG);
            requestQueue.add(request);
        }
    }

    private void handlePendingRequest(Consumer<List<PriceModel>> onSuccess) {
        synchronized (prices) {
            if (pendingRequests.decrementAndGet() == 0) {
                List<PriceModel> sortedPrices = prices.stream()
                    .sorted(Comparator.comparingDouble(PriceModel::getValue))
                    .limit(MAX_PRICES_TO_SHOW)
                    .collect(Collectors.toList());
                Log.d(TAG, "All prices received. Returning " + sortedPrices.size() + " cheapest prices in " + DEFAULT_CURRENCY);
                onSuccess.accept(sortedPrices);
            }
        }
    }

    public void cleanup() {
        requestQueue.cancelAll(TAG);
        requestQueue.stop();
    }
}
