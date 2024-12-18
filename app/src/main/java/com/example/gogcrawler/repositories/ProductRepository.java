package com.example.gogcrawler.repositories;

import com.android.volley.RequestQueue;
import com.example.gogcrawler.data.GOGApiSource;
import com.example.gogcrawler.data.ProductData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductRepository {
    private static final Pattern ID_PATTERN = Pattern.compile("card-product=\"(\\d+)\"");
    private static final String TAG = "ProductRepository";
    private final GOGApiSource apiSource;

    public ProductRepository(RequestQueue requestQueue) {
        this.apiSource = new GOGApiSource(requestQueue);
    }

    public void extractProductData(String url, OnProductExtractedListener listener) {
        if (!url.startsWith("https://www.gog.com/")) {
            listener.onError("Invalid URL: must start with https://www.gog.com/");
            return;
        }

        apiSource.fetchProductData(
                url,
                response -> {
                    Matcher idMatcher = ID_PATTERN.matcher(response);
                    String productId = null;
                    while (idMatcher.find()) {
                        productId = idMatcher.group(1);
                    }

                    if (productId == null) {
                        listener.onError("Invalid product id");
                        return;
                    }

                    // В будущем здесь можно добавить извлечение title и других данных
                    ProductData productData = new ProductData(productId, "");
                    listener.onProductExtracted(productData);
                },
                error -> listener.onError(error.getMessage())
        );
    }

    public void cancelRequests() {
        apiSource.cancelRequests();
    }

    public interface OnProductExtractedListener {
        void onProductExtracted(ProductData productData);

        void onError(String error);
    }
}
