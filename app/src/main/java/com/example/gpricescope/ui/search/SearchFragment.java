package com.example.gpricescope.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gpricescope.databinding.FragmentSearchBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RequestQueue requestQueue;
    private final List<PriceItem> prices = new ArrayList<>();
    private String productId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        requestQueue = Volley.newRequestQueue(requireContext());

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchPrices(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchPrices(String query) {
        prices.clear();
        if (!query.startsWith("https://www.gog.com/")) Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
        extractProductId(query);
    }

    private void extractProductId(String url) {
        StringRequest request = new StringRequest(url,
                response -> {
                    String regex = "card-product=\"(\\d+)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(response);
                    while (matcher.find()) productId = matcher.group(1);
                    if (productId == null) return;
                    for (String code : Countries.codes.keySet()) {
                        makeRequest("https://api.gog.com/products/" + productId + "/prices?countryCode=" + code + "&currency=USD");
                    }
                    Log.d("productId", productId);
                }, error -> Log.e("Error", error.toString()));
        requestQueue.add(request);
    }

    private void makeRequest(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject result = new JSONObject(response)
                        .getJSONObject("_embedded")
                        .getJSONArray("prices")
                        .getJSONObject(0);
                prices.add(
                        new PriceItem(url.split("countryCode=")[1].split("&")[0],
                                Integer.parseInt(result.getString("finalPrice").split(" ")[0]) / 100.00,
                                result.getString("finalPrice").split(" ")[1]));
                prices.sort(Comparator.comparing(PriceItem::getValue));
                if (prices.size() > 10) prices.subList(10, prices.size()).clear();
                binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.priceRecyclerView.setAdapter(new PriceAdapter(getContext(), prices));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        requestQueue.add(request);

    }
}