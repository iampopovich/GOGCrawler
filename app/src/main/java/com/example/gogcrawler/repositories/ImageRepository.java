package com.example.gogcrawler.repositories;

import com.android.volley.RequestQueue;
import com.example.gogcrawler.data.GOGImagesSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageRepository {
    private final GOGImagesSource imageSource;
    private static final Pattern IMG_PATTERN = 
            Pattern.compile("https://images\\.gog-statics\\.com/[^\\s,]+_product_card_v2_logo[^\\s,]+");

    public ImageRepository(RequestQueue requestQueue) {
        this.imageSource = new GOGImagesSource(requestQueue);
    }

    public void extractImageUrl(String htmlContent, 
                              OnImageExtractedListener listener) {
        imageSource.fetchImageData(
            htmlContent,
            response -> {
                Matcher imgMatcher = IMG_PATTERN.matcher(response);
                String imageUrl = imgMatcher.find() ? imgMatcher.group(0) : null;
                listener.onImageExtracted(imageUrl);
            },
            error -> listener.onError(error.getMessage())
        );
    }

    public interface OnImageExtractedListener {
        void onImageExtracted(String imageUrl);
        void onError(String error);
    }
}
