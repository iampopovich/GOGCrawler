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
// import com.example.gogcrawler.data.models.Countries; // Countries import seems unused

/**
 * The main activity of the application.
 * This activity is responsible for displaying the user interface for searching GOG products,
 * showing product details (like image and prices in different regions), and handling user interactions.
 * It utilizes {@link MainViewModel} to fetch and manage data.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private PriceAdapter priceAdapter;

    /**
     * Called when the activity is first created.
     * This method initializes the view binding, sets the content view,
     * and calls helper methods to set up UI components, ViewModel observers,
     * and handle any incoming intents.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}. Otherwise, it is null.
     */
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

    /**
     * Sets up window insets to adjust padding based on system bars.
     * This ensures that UI elements are not obscured by system UI like the status bar or navigation bar.
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    // Get insets for system bars (status bar, navigation bar)
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Apply padding to the main view
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets; // Return the insets to be consumed
                });
    }

    /**
     * Initializes the RecyclerView used to display prices.
     * Sets up its adapter and layout manager.
     */
    private void setupRecyclerView() {
        priceAdapter = new PriceAdapter(this, null); // Initialize adapter (data will be supplied by ViewModel)
        binding.priceRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set linear layout manager
        binding.priceRecyclerView.setAdapter(priceAdapter); // Set the adapter to the RecyclerView
    }

    /**
     * Initializes the {@link MainViewModel} and sets up observers for LiveData.
     * Observes changes in prices, image URL, errors, and loading state to update the UI accordingly.
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Observe changes in the list of prices
        viewModel.getPrices().observe(this, priceList -> {
            if (priceAdapter != null) {
                priceAdapter.updatePrices(priceList); // Update adapter data
            }
        });

        // Observe changes in the product image URL
        viewModel.getImageUrl().observe(this, imageUrl -> {
            if (imageUrl != null) {
                // Load image using Glide library into the ImageView
                Glide.with(this).load(imageUrl).into(binding.imageView);
            }
        });

        // Observe any errors reported by the ViewModel
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                // Display an error dialog
                new AlertDialog.Builder(this)
                    .setTitle(R.string.error_dialog_title) // Using string resource for title
                    .setMessage(error)
                    .setPositiveButton(android.R.string.ok, null) // Using Android's default OK
                    .show();
            }
        });

        // Observe loading state changes (e.g., to show/hide a progress bar)
        viewModel.getLoading().observe(this, isLoading -> {
            // Example: binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Currently, no specific loading indicator is implemented in the layout.
        });
    }

    /**
     * Sets up the SearchView for product searching.
     * Configures the query text listener to trigger searches in the ViewModel.
     */
    private void setupSearchView() {
        binding.searchView.clearFocus(); // Remove focus from search view initially
        binding.searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // When query is submitted, trigger search in ViewModel
                        viewModel.searchProduct(query);
                        binding.searchView.clearFocus(); // Remove focus from search view
                        return true; // Indicate the event was handled
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // React to text changes if needed, for now, we only care about submission
                        return true; // Indicate the event was handled (or false if default handling is desired)
                    }
                });
    }

    /**
     * Handles incoming intents, specifically for {@link Intent#ACTION_SEND} actions with "text/plain" data.
     * This allows the app to receive shared GOG game URLs from other apps (e.g., browsers).
     *
     * @param intent The new intent that was started for the activity.
     */
    private void handleIntent(Intent intent) {
        // Check if the intent is an ACTION_SEND intent with "text/plain" data
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT); // Get the shared text
                // Validate if the shared text is a GOG game URL
                if (sharedText != null
                        && sharedText.startsWith("https://www.gog.com/") // Must be a GOG URL
                        && sharedText.contains("/game/")) { // Must contain "/game/" path segment
                    // If valid, trigger product search in ViewModel
                    viewModel.searchProduct(sharedText);
                }
            }
        }
    }
}
