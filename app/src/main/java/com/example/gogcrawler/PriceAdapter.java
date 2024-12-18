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

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    private final Context context;
    private List<PriceItem> priceList;

    public PriceAdapter(Context context, List<PriceItem> priceList) {
        this.priceList = priceList != null ? priceList : new ArrayList<>();
        this.context = context;
    }

    public void setPrices(List<PriceItem> newPrices) {
        this.priceList = newPrices != null ? newPrices : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.price_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PriceAdapter.ViewHolder holder, int position) {
        PriceItem item = priceList.get(position);
        holder.countryTextView.setText(item.getCountry());
        holder.priceTextView.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView countryTextView;
        public final TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryTextView = itemView.findViewById(R.id.countryTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
