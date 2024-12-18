package com.example.gogcrawler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gogcrawler.data.PriceData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<PriceItem>> mPriceList = new MutableLiveData<>(new ArrayList<>());
    private final int limit = 10;

    public LiveData<List<PriceItem>> getPriceList() {
        return mPriceList;
    }

    public void clearPrices() {
        if (mPriceList.getValue() != null) {
            mPriceList.getValue().clear();
            mPriceList.setValue(new ArrayList<>());
        }
    }

    public void addPrice(PriceData price) {
        List<PriceItem> list = Objects.requireNonNull(mPriceList.getValue());
        list.add(new PriceItem(price.getCountryCode(), price.getPrice()));
        list.sort(Comparator.comparing(PriceItem::getValue));
        if (list.size() > limit) {
            list = list.subList(0, limit);
        }
        mPriceList.setValue(new ArrayList<>(list));
    }
}
