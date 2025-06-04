package com.example.gogcrawler.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gogcrawler.data.models.PriceItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * ViewModel responsible for managing and providing a sorted list of {@link PriceItem} objects.
 * This ViewModel is likely used in a search context where multiple price results might be found,
 * and the UI needs to display a limited number of the most relevant (e.g., cheapest) prices.
 * It keeps the list of prices sorted and caps its size.
 */
public class SearchViewModel extends ViewModel {
    // LiveData holding the list of price items.
    // This list is kept sorted by price value and limited in size.
    private final MutableLiveData<List<PriceItem>> mPriceList;

    /**
     * Constructor for SearchViewModel.
     * Initializes the LiveData with an empty list of price items.
     */
    public SearchViewModel() {
        mPriceList = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Adds a new {@link PriceItem} to the managed list.
     * The list is then re-sorted in ascending order based on {@link PriceItem#getValue()}.
     * If the list size exceeds 7 after adding, it is truncated to keep only the 7 items
     * with the lowest price values. Finally, the LiveData is updated.
     *
     * @param priceItem The {@link PriceItem} to add.
     */
    public void addPrice(PriceItem priceItem) {
        List<PriceItem> list = mPriceList.getValue();
        // Ensure the list is not null, though it's initialized in the constructor.
        Objects.requireNonNull(list, "Price list should not be null").add(priceItem);
        // Sort the list by price value in ascending order.
        list.sort(Comparator.comparing(PriceItem::getValue));
        // Limit the list to a maximum of 7 items (presumably the 7 cheapest).
        if (list.size() > 7) {
            list.subList(7, list.size()).clear(); // Remove items beyond the 7th
        }
        mPriceList.setValue(list); // Update the LiveData to notify observers.
    }

    /**
     * Clears all {@link PriceItem} objects from the managed list.
     * Updates the LiveData to reflect the empty list.
     */
    public void clearPrices() {
        List<PriceItem> list = mPriceList.getValue();
        Objects.requireNonNull(list, "Price list should not be null").clear();
        mPriceList.setValue(list); // Update LiveData with the now empty list.
    }

    /**
     * Returns the LiveData containing the list of {@link PriceItem} objects.
     * UI components can observe this LiveData to react to changes in the price list.
     *
     * @return A {@link MutableLiveData} instance containing the list of {@link PriceItem}s.
     */
    public MutableLiveData<List<PriceItem>> getPriceList() {
        return mPriceList;
    }
}
