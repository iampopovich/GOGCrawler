package com.example.gogcrawler;

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
        List<PriceItem> list = mPriceList.getValue();
        Objects.requireNonNull(list).add(priceItem);
        list.sort(Comparator.comparing(PriceItem::getValue));
        if (list.size() > 7) list.subList(7, list.size()).clear();
        mPriceList.setValue(list);
    }

    public void clearPrices() {
        List<PriceItem> list = mPriceList.getValue();
        Objects.requireNonNull(list).clear();
        mPriceList.setValue(list);
    }

    public MutableLiveData<List<PriceItem>> getPriceList() {
        return mPriceList;
    }
}