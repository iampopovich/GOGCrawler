package com.example.gpricescope.ui.search;

import android.content.Intent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private RequestQueue requestQueue;
    private String productId;
    private SearchViewModel searchViewModel;
    private final String TAG = "SearchFragment";
    private final String FETCH_PRICE_REQUEST_TAG = "fetch_price_request";
    private final String EXTRACT_PRODUCT_ID_REQUEST_TAG = "extract_product_id_request";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
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
        Intent intent = requireActivity().getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                handleSendText(intent, binding.searchView);
            }
        }

        binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.priceRecyclerView.setAdapter(new PriceAdapter(getContext(), searchViewModel.getPriceList()));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        requestQueue.stop();
        requestQueue.cancelAll(FETCH_PRICE_REQUEST_TAG);
        requestQueue.cancelAll(EXTRACT_PRODUCT_ID_REQUEST_TAG);
    }

    private void fetchPrices(String query) {
        requestQueue.cancelAll(FETCH_PRICE_REQUEST_TAG);
        requestQueue.cancelAll(EXTRACT_PRODUCT_ID_REQUEST_TAG);
        searchViewModel.clearPrices();
        if (!query.startsWith("https://www.gog.com/"))
            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
        extractProductId(query);
    }

    private void extractProductId(String url) {
        StringRequest request = new StringRequest(url,
                response -> {
                    String regex = "card-product=\"(\\d+)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(response);
                    while (matcher.find()) productId = matcher.group(1);
                    if (productId == null) {
                        Toast.makeText(getContext(), "Invalid product id", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Invalid product id");
                        return;
                    }
                    for (String code : Countries.codes.keySet()) {
                        makeRequest("https://api.gog.com/products/" + productId + "/prices?countryCode=" + code + "&currency=USD");
                    }
                    Log.d(TAG, productId);
                }, error -> Log.e(TAG, error.toString()));
        request.setTag(EXTRACT_PRODUCT_ID_REQUEST_TAG);
        requestQueue.add(request);
    }

    private void makeRequest(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject result = new JSONObject(response)
                        .getJSONObject("_embedded")
                        .getJSONArray("prices")
                        .getJSONObject(0);
                searchViewModel.addPrice(
                        new PriceItem(url.split("countryCode=")[1].split("&")[0],
                                Integer.parseInt(result.getString("finalPrice").split(" ")[0]) / 100.00));
//                                result.getString("finalPrice").split(" ")[1]));
                binding.priceRecyclerView.getAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
        });
        request.setTag(FETCH_PRICE_REQUEST_TAG);
        requestQueue.add(request);
    }

    private void handleSendText(Intent intent, SearchView searchView) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && sharedText.startsWith("https://www.gog.com/") && sharedText.contains("/game/")) {
            searchView.setQuery(sharedText, true);
            fetchPrices(sharedText);
        }
    }
}