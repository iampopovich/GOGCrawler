package com.example.gogcrawler.domain.usecases;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gogcrawler.data.models.PriceModel;
import com.example.gogcrawler.data.models.Countries;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Use case for fetching product prices from the GOG API for multiple countries.
 * This class orchestrates multiple network requests to the GOG API, one for each country defined
 * in {@link com.example.gogcrawler.data.models.Countries}.
 * It collects all successful price responses, sorts them, and returns a limited list of the cheapest prices.
 * The prices are fetched in a predefined currency (USD).
 */
public class GetPricesUseCase {
    private final RequestQueue requestQueue; // Volley request queue for network operations
    private static final String TAG = "GetPricesUseCase"; // Logcat tag
    private static final int MAX_PRICES_TO_SHOW = 10; // Maximum number of prices to return after sorting
    private static final String DEFAULT_CURRENCY = "USD"; // Currency for fetching prices

    // List to store fetched prices from various countries. Access to this list is synchronized.
    private final List<PriceModel> prices = new ArrayList<>();
    // Counter for pending network requests. Used to determine when all requests have completed.
    private final AtomicInteger pendingRequests = new AtomicInteger(0);

    /**
     * Constructor for GetPricesUseCase.
     *
     * @param context The application context, used to initialize the Volley request queue.
     */
    public GetPricesUseCase(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Executes the use case to fetch prices for a given product ID across multiple countries.
     *
     * @param productId The GOG product ID for which to fetch prices.
     * @param onSuccess A {@link Consumer} callback that is invoked when all price fetching attempts
     *                  are complete. It receives a list of {@link PriceModel} objects, sorted by price
     *                  and limited by {@code MAX_PRICES_TO_SHOW}. This list may be empty or partial
     *                  if some requests fail.
     * @param onError   A {@link Consumer} callback for reporting errors. Note: This specific implementation
     *                  does not directly call this onError callback for individual request failures;
     *                  it attempts to complete all requests and calls onSuccess with whatever data was gathered.
     *                  A more robust error handling might involve invoking onError if, for example,
     *                  all requests fail or a critical error occurs.
     */
    public void execute(String productId, Consumer<List<PriceModel>> onSuccess, Consumer<String> onError) {
        // Clear previous results and reset pending request counter
        prices.clear();
        pendingRequests.set(Countries.codes.size()); // Set counter to the number of countries
        
        // Iterate over each country code to fetch its price
        for (String code : Countries.codes.keySet()) {
            String url = String.format("https://api.gog.com/products/%s/prices?countryCode=%s&currency=%s",
                    productId, code, DEFAULT_CURRENCY);
            Log.d(TAG, "Fetching price for " + Countries.getCountry(code) + " (" + code + "): " + url);
            
            StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Successful network response
                    try {
                        // Parse the JSON response to extract price information
                        JSONObject result = new JSONObject(response)
                                .getJSONObject("_embedded")
                                .getJSONArray("prices")
                                .getJSONObject(0);
                        
                        String finalPrice = result.getString("finalPrice"); // Price format e.g., "1999 USD"
                        String[] parts = finalPrice.split(" ");
                        double price = Double.parseDouble(parts[0]) / 100.0; // Convert from cents to main unit
                        
                        Log.d(TAG, "Price received for " + Countries.getCountry(code) + ": " + price + " " + DEFAULT_CURRENCY);
                        
                        // Synchronize access to the shared prices list
                        synchronized (prices) {
                            prices.add(new PriceModel(code, price));
                            // If this was the last pending request, process and return results
                            if (pendingRequests.decrementAndGet() == 0) {
                                processAndReturnResults(onSuccess);
                            }
                        }
                    } catch (Exception e) {
                        // Error during JSON parsing or data extraction
                        Log.w(TAG, "Error parsing price for " + Countries.getCountry(code) + ": " + e.getMessage(), e);
                        handleRequestCompletion(onSuccess); // Still need to decrement counter and potentially finish
                    }
                },
                errorResponse -> {
                    // Network error for this specific request
                    Log.e(TAG, "Error fetching price for " + Countries.getCountry(code) + ": " + errorResponse.toString());
                    handleRequestCompletion(onSuccess); // Decrement counter and potentially finish
                }
            );
            request.setTag(TAG); // Tag request for potential cancellation
            requestQueue.add(request); // Add request to Volley queue
        }
    }

    /**
     * Handles the completion of a single request (either success with parsing error, or network error).
     * It decrements the pending request counter and, if all requests are accounted for,
     * triggers the processing and return of results.
     *
     * @param onSuccess The callback to invoke if all requests are complete.
     */
    private void handleRequestCompletion(Consumer<List<PriceModel>> onSuccess) {
        synchronized (prices) {
            if (pendingRequests.decrementAndGet() == 0) {
                processAndReturnResults(onSuccess);
            }
        }
    }

    /**
     * Sorts the collected prices, limits them to {@code MAX_PRICES_TO_SHOW},
     * and invokes the onSuccess callback. This method is called once all pending requests have completed.
     *
     * @param onSuccess The callback to provide the final list of prices.
     */
    private void processAndReturnResults(Consumer<List<PriceModel>> onSuccess) {
        List<PriceModel> sortedPrices = prices.stream()
            .sorted(Comparator.comparingDouble(PriceModel::getValue)) // Sort by price value
            .limit(MAX_PRICES_TO_SHOW) // Limit to the top N cheapest prices
            .collect(Collectors.toList());
        Log.d(TAG, "All price requests finished. Returning " + sortedPrices.size() + " cheapest prices in " + DEFAULT_CURRENCY);
        onSuccess.accept(sortedPrices); // Provide the final list to the callback
    }

    /**
     * Cleans up resources used by this use case.
     * Specifically, it cancels any pending Volley network requests associated with this use case's tag
     * and stops the request queue. This should be called when the use case is no longer needed
     * to prevent memory leaks and unnecessary network activity.
     */
    public void cleanup() {
        Log.d(TAG, "Cleaning up GetPricesUseCase: Cancelling pending requests.");
        requestQueue.cancelAll(TAG); // Cancel requests with this use case's tag
        // It's generally not recommended to stop the queue if it's shared and might be used by other parts
        // of the app. However, if this queue is exclusive to this use case, stopping is fine.
        // For this example, assuming it might be okay, but in a larger app, manage queue lifecycle carefully.
        // requestQueue.stop(); // Consider if stopping the queue is appropriate here.
    }
}
