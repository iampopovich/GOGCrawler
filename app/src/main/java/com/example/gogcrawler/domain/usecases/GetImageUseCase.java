package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use case for extracting a product image URL from a GOG game page URL.
 * This class fetches the HTML content of the given GOG URL and parses it
 * using a regular expression to find a specific product image URL,
 * typically the main product card logo.
 */
public class GetImageUseCase {
    private final RequestQueue requestQueue; // Volley request queue for network operations
    private static final String TAG = "GetImageUseCase"; // Logcat tag for logging

    /**
     * Constructor for GetImageUseCase.
     *
     * @param context The application context, used to initialize the Volley request queue.
     */
    public GetImageUseCase(Context context) {
        // Using getApplicationContext() for the RequestQueue to prevent context leaks.
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Executes the use case to find and return a GOG product image URL from a given GOG page URL.
     *
     * @param url       The GOG product page URL from which to extract the image URL.
     *                  It's assumed this URL has already been validated as a GOG URL if necessary
     *                  by the calling code (e.g., via GetProductUseCase).
     * @param onSuccess A {@link Consumer} callback that is invoked with the found image URL.
     * @param onError   A {@link Consumer} callback that is invoked if an error occurs,
     *                  such as a network error or if the image URL cannot be found.
     */
    public void execute(String url, Consumer<String> onSuccess, Consumer<String> onError) {
        // Create a Volley StringRequest to fetch the HTML content of the page
        StringRequest request = new StringRequest(Request.Method.GET, url,
            response -> {
                // HTML response received successfully
                // Regex to find a specific GOG product image URL (product card version 2 logo).
                // This pattern looks for URLs from "images.gog-statics.com" containing "_product_card_v2_logo".
                Pattern imgPattern = Pattern.compile("https://images\\.gog-statics\\.com/[^\\s,]+_product_card_v2_logo[^\\s,]+");
                Matcher imgMatcher = imgPattern.matcher(response);

                if (imgMatcher.find()) {
                    // Image URL found, invoke success callback with the first match
                    onSuccess.accept(imgMatcher.group(0));
                } else {
                    // If no matching image URL was found in the HTML
                    onError.accept("Could not find product image URL on the page: " + url);
                }
            },
            // Network error or other Volley error
            error -> onError.accept("Network error while fetching page for image extraction: " + error.toString())
        );
        // Add a tag to the request for potential cancellation
        request.setTag(TAG);
        requestQueue.add(request); // Add the request to Volley's queue
    }

    /**
     * Cleans up resources used by this use case.
     * This method should cancel any pending requests and can stop the request queue
     * if it's exclusively used by this use case.
     */
    public void cleanup() {
        // Cancel all requests tagged with this UseCase's TAG.
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
            // As with other use cases, consider the implications of stopping a shared queue.
            // requestQueue.stop();
        }
    }
}
