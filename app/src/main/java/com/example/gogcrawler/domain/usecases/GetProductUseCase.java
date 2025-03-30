package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetProductUseCase {
    private final RequestQueue requestQueue;
    private static final String TAG = "GetProductUseCase";

    public GetProductUseCase(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void execute(String url, Consumer<String> onSuccess, Consumer<String> onError) {
        if (!url.startsWith("https://www.gog.com/")) {
            onError.accept("Invalid URL: Must be a GOG.com URL");
            return;
        }

        StringRequest request = new StringRequest(url,
            response -> {
                Pattern idPattern = Pattern.compile("card-product=\"(\\d+)\"");
                Matcher idMatcher = idPattern.matcher(response);
                String productId = null;
                while (idMatcher.find()) {
                    productId = idMatcher.group(1);
                }
                
                if (productId == null) {
                    onError.accept("Could not find product ID");
                    return;
                }
                
                onSuccess.accept(productId);
            },
            error -> onError.accept(error.toString())
        );
        requestQueue.add(request);
    }

    public void cleanup() {
        requestQueue.stop();
    }
}
