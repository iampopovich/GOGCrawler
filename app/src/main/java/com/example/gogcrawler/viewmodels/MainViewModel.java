package com.example.gogcrawler.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gogcrawler.data.models.PriceModel;
import com.example.gogcrawler.domain.usecases.GetProductUseCase;
import com.example.gogcrawler.domain.usecases.GetPricesUseCase;
import com.example.gogcrawler.domain.usecases.GetImageUseCase;
import java.util.List;

/**
 * ViewModel for {@link com.example.gogcrawler.ui.main.MainActivity}.
 * This class is responsible for preparing and managing the data for the UI.
 * It interacts with various use cases ({@link GetProductUseCase}, {@link GetPricesUseCase}, {@link GetImageUseCase})
 * to fetch product details, prices across different regions, and product images from GOG.com.
 * It exposes data via {@link LiveData} to be observed by the MainActivity.
 */
public class MainViewModel extends AndroidViewModel {
    private final GetProductUseCase getProductUseCase;
    private final GetPricesUseCase getPricesUseCase;
    private final GetImageUseCase getImageUseCase;
    
    // LiveData to hold the list of prices for different countries/regions
    private final MutableLiveData<List<PriceModel>> prices = new MutableLiveData<>();
    // LiveData to hold the URL of the product image
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    // LiveData to hold any error messages that occur during data fetching
    private final MutableLiveData<String> error = new MutableLiveData<>();
    // LiveData to indicate whether data is currently being loaded
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    /**
     * Constructor for MainViewModel.
     * Initializes the ViewModel and its use case dependencies.
     *
     * @param application The application context.
     */
    public MainViewModel(Application application) {
        super(application);
        // Initialize use cases required by this ViewModel
        this.getProductUseCase = new GetProductUseCase(application);
        this.getPricesUseCase = new GetPricesUseCase(application);
        this.getImageUseCase = new GetImageUseCase(application);
    }

    /**
     * Initiates a search for a GOG product based on the provided URL.
     * It first fetches the product ID, then uses this ID to get regional prices,
     * and also fetches the product image. Updates LiveData for UI.
     *
     * @param url The GOG product page URL.
     */
    public void searchProduct(String url) {
        loading.setValue(true); // Signal start of loading
        clearPrices(); // Clear previous price results
        imageUrl.setValue(null); // Clear previous image
        error.setValue(null); // Clear previous errors
        
        // Step 1: Get product ID from the URL
        getProductUseCase.execute(url,
            productId -> {
                // Step 2a: Product ID fetched successfully, now get prices
                getPricesUseCase.execute(productId,
                    priceList -> {
                        prices.postValue(priceList); // Post new prices
                        // Consider setting loading to false only after all operations complete
                        // For now, individual operations might toggle it.
                    },
                    // Error callback for GetPricesUseCase
                    priceError -> handleError("Error fetching prices: " + priceError)
                );
                
                // Step 2b: Get product image using the original URL
                getImageUseCase.execute(url,
                    imgUrl -> imageUrl.postValue(imgUrl), // Post new image URL
                    // Error callback for GetImageUseCase
                    imageError -> handleError("Error fetching image: " + imageError)
                );
                // Note: loading state is primarily handled by the completion/error of the product ID fetch
                // and then potentially by price fetching if it's the last main operation.
                // A more robust loading state would track all concurrent operations.
                loading.postValue(false); // Signal end of loading (might be premature if image/prices are slow)
            },
            // Error callback for GetProductUseCase
            productError -> handleError("Error fetching product: " + productError)
        );
    }

    /**
     * Handles errors by posting the message to the error LiveData and setting loading to false.
     *
     * @param errorMessage The error message to display.
     */
    private void handleError(String errorMessage) {
        error.postValue(errorMessage); // Post error message
        loading.postValue(false); // Ensure loading is stopped on error
    }

    /**
     * Returns LiveData holding the list of product prices.
     * UI can observe this to display prices.
     * @return LiveData list of {@link PriceModel}.
     */
    public LiveData<List<PriceModel>> getPrices() {
        return prices;
    }

    /**
     * Returns LiveData holding the product image URL.
     * UI can observe this to display the image.
     * @return LiveData String representing the image URL.
     */
    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns LiveData holding error messages.
     * UI can observe this to display errors to the user.
     * @return LiveData String representing an error message.
     */
    public LiveData<String> getError() {
        return error;
    }

    /**
     * Returns LiveData indicating the loading state.
     * UI can observe this to show/hide loading indicators.
     * @return LiveData Boolean, true if loading, false otherwise.
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    /**
     * Clears the current list of prices.
     * Typically called before a new search.
     */
    public void clearPrices() {
        prices.setValue(null); // Set prices to null to clear the list
    }

    /**
     * Called when the ViewModel is no longer used and will be destroyed.
     * This is the place to clean up any resources, such as cancelling ongoing network requests
     * by calling cleanup on the use cases.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up resources used by the use cases
        getProductUseCase.cleanup();
        getPricesUseCase.cleanup();
        getImageUseCase.cleanup();
    }
}
