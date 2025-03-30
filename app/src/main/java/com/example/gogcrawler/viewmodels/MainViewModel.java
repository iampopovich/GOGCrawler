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

public class MainViewModel extends AndroidViewModel {
    private final GetProductUseCase getProductUseCase;
    private final GetPricesUseCase getPricesUseCase;
    private final GetImageUseCase getImageUseCase;
    
    private final MutableLiveData<List<PriceModel>> prices = new MutableLiveData<>();
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        this.getProductUseCase = new GetProductUseCase(application);
        this.getPricesUseCase = new GetPricesUseCase(application);
        this.getImageUseCase = new GetImageUseCase(application);
    }

    public void searchProduct(String url) {
        loading.setValue(true);
        clearPrices();
        
        // Get product data and image
        getProductUseCase.execute(url,
            productId -> {
                // Get prices
                getPricesUseCase.execute(productId,
                    priceList -> {
                        prices.postValue(priceList);
                        loading.postValue(false);
                    },
                    error -> handleError("Error fetching prices: " + error)
                );
                
                // Get image
                getImageUseCase.execute(url,
                    imgUrl -> imageUrl.postValue(imgUrl),
                    error -> handleError("Error fetching image: " + error)
                );
            },
            error -> handleError("Error fetching product: " + error)
        );
    }

    private void handleError(String errorMessage) {
        error.postValue(errorMessage);
        loading.postValue(false);
    }

    public LiveData<List<PriceModel>> getPrices() {
        return prices;
    }

    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void clearPrices() {
        prices.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getProductUseCase.cleanup();
        getPricesUseCase.cleanup();
        getImageUseCase.cleanup();
    }
}
