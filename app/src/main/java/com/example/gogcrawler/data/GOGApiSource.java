package com.example.gogcrawler.data;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class GOGApiSource {
    private static final String TAG = "GOGApiSource";
    private final RequestQueue requestQueue;

    public GOGApiSource(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void fetchProductData(String url,
                                 Response.Listener<String> successListener,
                                 Response.ErrorListener errorListener) {
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
