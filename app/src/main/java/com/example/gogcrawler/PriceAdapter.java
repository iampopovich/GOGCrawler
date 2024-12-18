package com.example.gogcrawler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a list of prices in a RecyclerView.
 */
public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    // List of prices to be displayed
    private List<PriceItem> priceList;
    private final Context context;

    /**
     * Constructor for the PriceAdapter.
     *
     * @param context   Context for inflating layout
     * @param priceList List of prices to be displayed
     */
    public PriceAdapter(Context context, List<PriceItem> priceList) {
        this.context = context;
        this.priceList = priceList != null ? priceList : new ArrayList<>();
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param parent   ViewGroup parent
     * @param viewType View type
     * @return New ViewHolder instance
     */
    @NonNull
    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.price_item, parent, false));
    }

    /**
     * Binds data to the ViewHolder.
     *
     * @param holder   ViewHolder instance
     * @param position Position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull PriceAdapter.ViewHolder holder, int position) {
        PriceItem item = priceList.get(position);
        holder.countryTextView.setText(item.getCountry());
        holder.priceTextView.setText(item.getPrice());
    }

    /**
     * Returns the number of items in the list.
     *
     * @return Number of items in the list
     */
    @Override
    public int getItemCount() {
        return priceList.size();
    }

    /**
     * Updates the list of prices and notifies the adapter.
     *
     * @param newPrices New list of prices
     */
    public void setPrices(List<PriceItem> newPrices) {
        this.priceList = (newPrices != null) ? newPrices : new ArrayList<>(); // Simplified null check
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for the price item layout.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TextView for displaying country
        public final TextView countryTextView;
        // TextView for displaying price
        public final TextView priceTextView;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView View instance
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryTextView = itemView.findViewById(R.id.countryTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
