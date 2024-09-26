package com.example.gogcrawler;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gogcrawler.databinding.ActivityMainBinding;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private final String TAG = "SearchFragment";
  private final String FETCH_PRICE_REQUEST_TAG = "fetch_price_request";
  private final String EXTRACT_PRODUCT_ID_REQUEST_TAG = "extract_product_id_request";
  private ActivityMainBinding binding;
  private RequestQueue requestQueue;
  private String productId;
  private SearchViewModel searchViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    searchViewModel
        .getPriceList()
        .observe(
            this,
            priceList -> {
              binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
              binding.priceRecyclerView.setAdapter(new PriceAdapter(this, priceList));
            });

    requestQueue = Volley.newRequestQueue(this);

    binding.searchView.clearFocus();
    binding.searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            fetchPrices(query);
            return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            return true;
          }
        });
    Intent intent = this.getIntent();
    if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
      if ("text/plain".equals(intent.getType())) {
        handleSendText(intent, binding.searchView);
      }
    }
  }

  private void fetchPrices(String query) {
    requestQueue.cancelAll(FETCH_PRICE_REQUEST_TAG);
    requestQueue.cancelAll(EXTRACT_PRODUCT_ID_REQUEST_TAG);
    searchViewModel.clearPrices();
    if (!query.startsWith("https://www.gog.com/")) {
      new AlertDialog.Builder(this)
          .setTitle("Invalid URL error")
          .setMessage("Check if you entered correct URL")
          .show();
      return;
    }
    extractProductData(query);
  }

  private void extractProductData(String url) {

    StringRequest request =
        new StringRequest(
            url,
            response -> {
              Pattern idPattern = Pattern.compile("card-product=\"(\\d+)\"");
              Matcher idMatcher = idPattern.matcher(response);
              while (idMatcher.find()) productId = idMatcher.group(1);
              if (productId == null) {
                new AlertDialog.Builder(this)
                    .setTitle("Invalid product id")
                    .setMessage("Check if you entered correct URL")
                    .show();
                Log.e(TAG, "Invalid product id");
                return;
              }
              Pattern imgPattern =
                  Pattern.compile(
                      "https://images\\.gog-statics\\.com/[^\\s,]+_product_card_v2_logo[^\\s,]+");
              Matcher imgMatcher = imgPattern.matcher(response);
              if (imgMatcher.find()) {
                Glide.with(this).load(imgMatcher.group(0)).into(binding.imageView);
              }
              Log.d(TAG, productId);
              for (String code : Countries.codes.keySet()) {
                makeRequest(
                    "https://api.gog.com/products/"
                        + productId
                        + "/prices?countryCode="
                        + code
                        + "&currency=USD");
              }
            },
            error -> Log.e(TAG, error.toString()));
    request.setTag(EXTRACT_PRODUCT_ID_REQUEST_TAG);
    requestQueue.add(request);
  }

  private void makeRequest(String url) {
    StringRequest request =
        new StringRequest(
            Request.Method.GET,
            url,
            response -> {
              try {
                JSONObject result =
                    new JSONObject(response)
                        .getJSONObject("_embedded")
                        .getJSONArray("prices")
                        .getJSONObject(0);
                searchViewModel.addPrice(
                    new PriceItem(
                        url.split("countryCode=")[1].split("&")[0],
                        Integer.parseInt(result.getString("finalPrice").split(" ")[0]) / 100.00));
              } catch (JSONException e) {
                throw new RuntimeException(e);
              }
            },
            error -> {});
    request.setTag(FETCH_PRICE_REQUEST_TAG);
    requestQueue.add(request);
  }

  private void handleSendText(Intent intent, SearchView searchView) {
    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    if (sharedText != null
        && sharedText.startsWith("https://www.gog.com/")
        && sharedText.contains("/game/")) {
      searchView.setQuery(sharedText, false);
      fetchPrices(sharedText);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    requestQueue.stop();
    requestQueue.cancelAll(FETCH_PRICE_REQUEST_TAG);
    requestQueue.cancelAll(EXTRACT_PRODUCT_ID_REQUEST_TAG);
  }
}
