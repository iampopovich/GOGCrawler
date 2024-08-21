package com.example.gpricescope.ui.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<PriceItem>> mPriceList;

    public SearchViewModel() {
        mPriceList = new MutableLiveData<>(new ArrayList<>());
    }

    public void addPrice(PriceItem priceItem) {
        Objects.requireNonNull(mPriceList.getValue()).add(priceItem);
        mPriceList.getValue().sort(Comparator.comparing(PriceItem::getValue));
        if (mPriceList.getValue().size() > 7)
            mPriceList.getValue().subList(7, mPriceList.getValue().size()).clear();
    }

    public void clearPrices() {
        Objects.requireNonNull(mPriceList.getValue()).clear();
    }

    public List<PriceItem> getPriceList() {
        return mPriceList.getValue();
    }
}