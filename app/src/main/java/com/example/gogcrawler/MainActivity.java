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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gogcrawler.data.PriceData;
import com.example.gogcrawler.data.ProductData;
import com.example.gogcrawler.databinding.ActivityMainBinding;
import com.example.gogcrawler.repositories.ImageRepository;
import com.example.gogcrawler.repositories.PriceRepository;
import com.example.gogcrawler.repositories.ProductRepository;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private SearchViewModel searchViewModel;
    private ImageRepository imageRepository;
    private ProductRepository productRepository;
    private PriceRepository priceRepository;

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
        imageRepository = new ImageRepository(requestQueue);
        productRepository = new ProductRepository(requestQueue);
        priceRepository = new PriceRepository(requestQueue);

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
        searchViewModel.clearPrices();

        productRepository.extractProductData(query, new ProductRepository.OnProductExtractedListener() {
            @Override
            public void onProductExtracted(ProductData productData) {
                // Получаем изображение
                imageRepository.extractImageUrl(productData.getId(), new ImageRepository.OnImageExtractedListener() {
                    @Override
                    public void onImageExtracted(String imageUrl) {
                        if (imageUrl != null) {
                            Glide.with(MainActivity.this).load(imageUrl).into(binding.imageView);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error extracting image: " + error);
                    }
                });

                // Запрашиваем цены
                priceRepository.fetchPrices(productData.getId(), new PriceRepository.OnPriceListener() {
                    @Override
                    public void onPriceReceived(PriceData price) {
                        searchViewModel.addPrice(price);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error fetching price: " + error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage(error)
                        .show();
            }
        });
    }
//todo: handleSendText not implemented yet
    private void handleSendText(Intent intent, SearchView searchView) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null
                && sharedText.startsWith("https://www.gog.com/")
                && sharedText.contains("/game/")) {
            searchView.setQuery(sharedText, false);
//            fetchPrices(sharedText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.stop();
        productRepository.cancelRequests();
        imageRepository.cancelRequests();
        priceRepository.cancelRequests();
    }
}
