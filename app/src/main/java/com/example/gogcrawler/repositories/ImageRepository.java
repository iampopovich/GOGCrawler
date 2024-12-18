package com.example.gogcrawler.repositories;

import com.android.volley.RequestQueue;
import com.example.gogcrawler.data.GOGImagesSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageRepository {
    private static final Pattern IMG_PATTERN =
            Pattern.compile("https://images\\.gog-statics\\.com/[^\\s,]+_product_card_v2_logo[^\\s,]+");
    private final GOGImagesSource imageSource;

    public ImageRepository(RequestQueue requestQueue) {
        this.imageSource = new GOGImagesSource(requestQueue);
    }

    public void extractImageUrl(String productId,
                                OnImageExtractedListener listener) {
        imageSource.fetchImageData(
                productId,
                response -> {
                    Matcher imgMatcher = IMG_PATTERN.matcher(response);
                    String imageUrl = imgMatcher.find() ? imgMatcher.group(0) : null;
                    listener.onImageExtracted(imageUrl);
                },
                error -> listener.onError(error.getMessage())
        );
    }

    public void cancelRequests() {
        imageSource.cancelRequests();
    }

    public interface OnImageExtractedListener {
        void onImageExtracted(String imageUrl);

        void onError(String error);
    }
}
