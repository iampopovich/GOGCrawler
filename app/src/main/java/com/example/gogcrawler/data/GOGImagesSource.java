package com.example.gogcrawler.data;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

public class GOGImagesSource {
    private static final String TAG = "GOGImagesSource";
    private static final String BASE_URL = "https://images.gog-statics.com/";
    private final RequestQueue requestQueue;

    public GOGImagesSource(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void fetchImageData(String htmlContent,
                               Response.Listener<String> successListener,
                               Response.ErrorListener errorListener) {
        //todo: implement request to fetch image data
        successListener.onResponse(htmlContent);
    }
}
