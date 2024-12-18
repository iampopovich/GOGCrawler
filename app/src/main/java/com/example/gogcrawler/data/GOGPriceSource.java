package com.example.gogcrawler.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class GOGPriceSource {
    private static final String TAG = "GOGPriceSource";
    private static final String BASE_URL = "https://api.gog.com/products/";
    private final RequestQueue requestQueue;

    public GOGPriceSource(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void fetchPrice(String productId,
                           String countryCode,
                           Response.Listener<String> successListener,
                           Response.ErrorListener errorListener) {
        String url = BASE_URL + productId + "/prices?countryCode=" + countryCode + "&currency=USD";
        Log.d(TAG, "Fetching price from: " + url);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                successListener,
                errorListener
        );
        request.setTag(TAG);
        requestQueue.add(request);
    }

    public void cancelRequests() {
        requestQueue.cancelAll(TAG);
    }
}
