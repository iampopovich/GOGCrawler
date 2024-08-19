package com.example.gpricescope.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<PriceItem>> mPriceList;

    public SearchViewModel() {
        mPriceList = new MutableLiveData<>();
    }

    public LiveData<List<PriceItem>> getPriceList() {
        return mPriceList;
    }

    public void setPrices(List<PriceItem> prices) {
        mPriceList.setValue(prices);
    }

}