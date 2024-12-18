package com.example.gogcrawler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gogcrawler.data.PriceData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<PriceItem>> mPriceList = new MutableLiveData<>(new ArrayList<>());
    private static final int limit = 10;

    public MutableLiveData<List<PriceItem>> getPriceList() {
        return mPriceList;
    }

    public void clearPrices() {
        List<PriceItem> list = mPriceList.getValue();
        if (list != null) {
            list.clear();
            mPriceList.setValue(list);
        }
    }

    public void updatePrices(List<PriceData> prices) {
        List<PriceItem> priceItems = new ArrayList<>();
        for (PriceData price : prices) {
            priceItems.add(new PriceItem(price.getCountryCode(), price.getPrice()));
        }
        priceItems.sort(Comparator.comparing(PriceItem::getValue));
        if (priceItems.size() > limit) {
            priceItems = priceItems.subList(0, limit);
        }
        mPriceList.setValue(priceItems);
    }
}
