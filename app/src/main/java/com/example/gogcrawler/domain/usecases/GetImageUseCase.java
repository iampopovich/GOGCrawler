package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetImageUseCase {
    private final RequestQueue requestQueue;
    private static final String TAG = "GetImageUseCase";

    public GetImageUseCase(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void execute(String url, Consumer<String> onSuccess, Consumer<String> onError) {
        StringRequest request = new StringRequest(url,
            response -> {
                Pattern imgPattern = Pattern.compile("https://images\\.gog-statics\\.com/[^\\s,]+_product_card_v2_logo[^\\s,]+");
                Matcher imgMatcher = imgPattern.matcher(response);
                if (imgMatcher.find()) {
                    onSuccess.accept(imgMatcher.group(0));
                } else {
                    onError.accept("Could not find product image");
                }
            },
            error -> onError.accept(error.toString())
        );
        requestQueue.add(request);
    }

    public void cleanup() {
        requestQueue.stop();
    }
}
