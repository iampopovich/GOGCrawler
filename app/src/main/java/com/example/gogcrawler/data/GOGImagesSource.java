package com.example.gogcrawler.data;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class GOGImagesSource {
    private static final String TAG = "GOGImagesSource";
    private static final String BASE_URL = "https://www.gog.com/game/";
    private final RequestQueue requestQueue;

    public GOGImagesSource(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void fetchImageData(String productId,
                               Response.Listener<String> successListener,
                               Response.ErrorListener errorListener) {
        String url = BASE_URL + productId;
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
