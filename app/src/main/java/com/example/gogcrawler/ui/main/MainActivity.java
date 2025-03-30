package com.example.gogcrawler.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.gogcrawler.R;
import com.example.gogcrawler.databinding.ActivityMainBinding;
import com.example.gogcrawler.viewmodels.MainViewModel;
import com.example.gogcrawler.data.models.Countries;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private PriceAdapter priceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        setupRecyclerView();
        setupViewModel();
        setupSearchView();
        handleIntent(getIntent());
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }

    private void setupRecyclerView() {
        priceAdapter = new PriceAdapter(this, null);
        binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.priceRecyclerView.setAdapter(priceAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Observe prices
        viewModel.getPrices().observe(this, priceList -> {
            if (priceAdapter != null) {
                priceAdapter.updatePrices(priceList);
            }
        });

        // Observe image URL
        viewModel.getImageUrl().observe(this, imageUrl -> {
            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).into(binding.imageView);
            }
        });

        // Observe errors
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("OK", null)
                    .show();
            }
        });

        // Observe loading state
        viewModel.getLoading().observe(this, isLoading -> {
            // You can add loading indicator here if needed
        });
    }

    private void setupSearchView() {
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.searchProduct(query);
                        binding.searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null
                        && sharedText.startsWith("https://www.gog.com/")
                        && sharedText.contains("/game/")) {
                    viewModel.searchProduct(sharedText);
                }
            }
        }
    }
}
