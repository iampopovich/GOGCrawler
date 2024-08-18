package com.example.gpricescope.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gpricescope.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RequestQueue requestQueue;
    private List<PriceItem> prices;
    private String productId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        requestQueue = Volley.newRequestQueue(this.getContext());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
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
        prices = new ArrayList<>();
        query = "https://www.gog.com/en/game/diablo";
        if (!query.startsWith("https://www.gog.com/")) return;
        extractProductId(query);
        if (productId == null) return;
        for (String code : Countries.codes.keySet()) {
            makeRequest("https://api.gog.com/products/" + productId + "/prices?countryCode=" + code + "&currency=USD");
            break;
        }
        binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        PriceAdapter adapter = new PriceAdapter(this.getContext(), prices);
        binding.priceRecyclerView.setAdapter(adapter);
    }

    private void extractProductId(String url) {
        StringRequest request = new StringRequest(url,
                response -> {
                    String regex = "card-product=\"(\\d+)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(response);
                    String productId = matcher.group(1);
                    assert productId != null;
                    Log.d("productId", productId);
                }, error -> {
            Log.e("Error", error.toString());
        });
        requestQueue.add(request);
    }

    private void makeRequest(String url) {
//        StringRequest request = new StringRequest(Request.Method.GET, url, response->{
//            try {
//                JSONObject result = new JSONObject(response);
//                String price = result.getString("price");
//                String currency = result.getString("currency");
//                String code = result.getString("countryCode");
//                prices.add(new PriceItem(code, price, currency));
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }, error -> {});
//        requestQueue.add(request);
    }
}