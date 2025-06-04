package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use case for extracting a GOG product ID from a GOG game page URL.
 * This class fetches the HTML content of the given GOG URL and parses it
 * using a regular expression to find the unique product identifier.
 */
public class GetProductUseCase {
    private final RequestQueue requestQueue; // Volley request queue for network operations
    private static final String TAG = "GetProductUseCase"; // Logcat tag for logging

    /**
     * Constructor for GetProductUseCase.
     *
     * @param context The application context, used to initialize the Volley request queue.
     */
    public GetProductUseCase(Context context) {
        // It's generally better to use getApplicationContext() when initializing singletons
        // or long-lived objects like a RequestQueue to avoid leaking Activity/Service context.
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Executes the use case to find and return the GOG product ID from a given URL.
     *
     * @param url       The GOG product page URL (e.g., "https://www.gog.com/game/some_game_title").
     * @param onSuccess A {@link Consumer} callback that is invoked with the found product ID.
     * @param onError   A {@link Consumer} callback that is invoked if an error occurs,
     *                  such as an invalid URL, network error, or if the product ID cannot be found.
     */
    public void execute(String url, Consumer<String> onSuccess, Consumer<String> onError) {
        // Basic URL validation
        if (!url.startsWith("https://www.gog.com/")) {
            onError.accept("Invalid URL: Must be a GOG.com URL that starts with https://www.gog.com/");
            return;
        }

        // Create a Volley StringRequest to fetch the HTML content of the page
        StringRequest request = new StringRequest(Request.Method.GET, url,
            response -> {
                // HTML response received successfully
                // Regex to find the product ID, typically found in an attribute like 'card-product="12345"'
                Pattern idPattern = Pattern.compile("card-product=\"(\\d+)\"");
                Matcher idMatcher = idPattern.matcher(response);
                String productId = null;
                // Loop through all matches; typically, we expect one, or the relevant one is often the last.
                while (idMatcher.find()) {
                    productId = idMatcher.group(1); // Group 1 captures the digits (the ID)
                }
                
                if (productId == null) {
                    // If no product ID was found in the HTML
                    onError.accept("Could not find product ID on the page: " + url);
                    return;
                }
                
                // Product ID found, invoke success callback
                onSuccess.accept(productId);
            },
            // Network error or other Volley error
            error -> onError.accept("Network error while fetching product page: " + error.toString())
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
            // Consider if stopping the queue is appropriate. If the queue is shared,
            // stopping it here could affect other operations.
            // For this example, we'll leave it commented out as a reminder.
            // requestQueue.stop();
        }
    }
}
