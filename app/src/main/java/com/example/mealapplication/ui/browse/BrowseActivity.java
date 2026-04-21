package com.example.mealapplication.ui.browse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.adapter.MealAdapter;
import com.example.mealapplication.model.Category;
import com.example.mealapplication.model.CategoryResponse;
import com.example.mealapplication.model.Meal;
import com.example.mealapplication.model.MealResponse;
import com.example.mealapplication.remote.ApiClient;
import com.example.mealapplication.ui.favourites.FavoritesActivity;
import com.example.mealapplication.ui.meals.MealListActivity;
import com.example.mealapplication.ui.planner.MealPlanActivity;
import com.example.mealapplication.ui.profile.ProfileActivity;
import com.example.mealapplication.utils.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity {

    // Filter chips: label → API category
    private static final String FILTER_ALL = "ALL";       // special: multi-category
    private static final String FILTER_VEGAN = "Vegan";
    private static final String FILTER_VEGETARIAN = "Vegetarian";

    // Categories to mix for "All / Featured" diversity
    private static final String[] FEATURED_CATEGORIES = {"Beef", "Pasta", "Chicken", "Dessert", "Lamb", "Vegetarian"};
    private static final int FEATURED_PER_CATEGORY = 2; // 2 × 6 cats = 12 possible, capped at 6

    private RecyclerView featuredRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private MealAdapter featuredAdapter;
    private CategoryAdapter categoriesAdapter;

    private View featuredProgressBar;
    private View featuredErrorLayout;
    private TextView featuredErrorText;

    private View categoriesProgressBar;
    private View categoriesErrorLayout;
    private TextView categoriesErrorText;

    private EditText searchEditText;
    private TextView chipAll, chipVegan, chipVegetarian;
    private TextView seeAllButton;
    private NestedScrollView contentScrollView;
    private HorizontalScrollView chipsScrollView;
    private BottomNavigationView bottomNav;

    private String currentFilter = FILTER_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        initViews();
        setupRecyclerViews();
        setupChips();
        setupSearch();
        setupNavigation();

        loadFeaturedMeals();
        loadCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_home);
        // Scroll back to top so filter chips are fully visible
        if (contentScrollView != null) contentScrollView.scrollTo(0, 0);
        if (chipsScrollView != null) chipsScrollView.scrollTo(0, 0);
    }

    private void initViews() {
        featuredRecyclerView = findViewById(R.id.featuredRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        featuredProgressBar = findViewById(R.id.featuredProgressBar);
        featuredErrorLayout = findViewById(R.id.featuredErrorLayout);
        featuredErrorText = findViewById(R.id.featuredErrorText);
        categoriesProgressBar = findViewById(R.id.categoriesProgressBar);
        categoriesErrorLayout = findViewById(R.id.categoriesErrorLayout);
        categoriesErrorText = findViewById(R.id.categoriesErrorText);
        searchEditText = findViewById(R.id.searchEditText);
        chipAll = findViewById(R.id.chipAll);
        chipVegan = findViewById(R.id.chipVegan);
        chipVegetarian = findViewById(R.id.chipVegetarian);
        seeAllButton = findViewById(R.id.seeAllButton);
        contentScrollView = findViewById(R.id.contentScrollView);
        chipsScrollView = findViewById(R.id.chipsScrollView);

        findViewById(R.id.featuredRetryButton).setOnClickListener(v -> loadFeaturedMeals());
        findViewById(R.id.categoriesRetryButton).setOnClickListener(v -> loadCategories());

        View accountButton = findViewById(R.id.accountButton);
        if (accountButton != null) {
            accountButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerViews() {
        featuredAdapter = new MealAdapter(meal -> openRecipeDetail(meal.getId()));
        featuredRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        featuredRecyclerView.setAdapter(featuredAdapter);

        categoriesAdapter = new CategoryAdapter(category -> {
            Intent intent = new Intent(BrowseActivity.this, MealListActivity.class);
            intent.putExtra(MealListActivity.EXTRA_CATEGORY, category.getName());
            intent.putExtra(MealListActivity.EXTRA_TITLE, category.getName());
            startActivity(intent);
        });
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesRecyclerView.setAdapter(categoriesAdapter);
    }

    private void setupChips() {
        chipAll.setOnClickListener(v -> selectChip(FILTER_ALL));
        chipVegan.setOnClickListener(v -> selectChip(FILTER_VEGAN));
        chipVegetarian.setOnClickListener(v -> selectChip(FILTER_VEGETARIAN));

        seeAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseActivity.this, MealListActivity.class);
            if (currentFilter.equals(FILTER_ALL)) {
                // Show all recipes
                intent.putExtra(MealListActivity.EXTRA_TITLE, getString(R.string.all_recipes));
            } else {
                intent.putExtra(MealListActivity.EXTRA_CATEGORY, currentFilter);
                intent.putExtra(MealListActivity.EXTRA_TITLE, getDisplayLabel(currentFilter));
            }
            startActivity(intent);
        });
    }

    private void setupSearch() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void setupNavigation() {
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            Intent intent;
            if (id == R.id.nav_recipes) {
                intent = new Intent(this, MealListActivity.class);
            } else if (id == R.id.nav_favorites) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (id == R.id.nav_planner) {
                intent = new Intent(this, MealPlanActivity.class);
            } else {
                return false;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        });
    }

    private void selectChip(String filter) {
        currentFilter = filter;
        updateChipVisuals();
        loadFeaturedMeals();
    }

    private void updateChipVisuals() {
        // Use setBackgroundResource per chip to avoid drawable-instance sharing
        // which corrupts corner radius when the same Drawable object is reused
        int selectedColor = ContextCompat.getColor(this, R.color.chip_selected_text);
        int unselectedColor = ContextCompat.getColor(this, R.color.chip_unselected_text);

        applyChip(chipAll, currentFilter.equals(FILTER_ALL), selectedColor, unselectedColor);
        applyChip(chipVegan, currentFilter.equals(FILTER_VEGAN), selectedColor, unselectedColor);
        applyChip(chipVegetarian, currentFilter.equals(FILTER_VEGETARIAN), selectedColor, unselectedColor);
    }

    private void applyChip(TextView chip, boolean active, int selectedColor, int unselectedColor) {
        chip.setBackgroundResource(active ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chip.setTextColor(active ? selectedColor : unselectedColor);
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            Intent intent = new Intent(BrowseActivity.this, MealListActivity.class);
            intent.putExtra(MealListActivity.EXTRA_SEARCH_QUERY, query);
            startActivity(intent);
        }
    }

    private void loadFeaturedMeals() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showFeaturedError(getString(R.string.error_no_internet));
            return;
        }
        showFeaturedLoading();

        if (currentFilter.equals(FILTER_ALL)) {
            loadDiverseFeatured();
        } else {
            ApiClient.getApiService().getMealsByCategory(currentFilter).enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                        List<Meal> meals = response.body().getMeals();
                        if (meals.size() > 6) meals = meals.subList(0, 6);
                        final List<Meal> finalMeals = meals;
                        featuredAdapter.setMeals(finalMeals);
                        featuredAdapter.setTag(getDisplayLabel(currentFilter));
                        showFeaturedContent();
                    } else {
                        showFeaturedError(getString(R.string.error_loading_data));
                    }
                }
                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    showFeaturedError(getString(R.string.error_loading_data));
                }
            });
        }
    }

    private void loadDiverseFeatured() {
        final List<Meal> combined = new ArrayList<>();
        final int[] remaining = {FEATURED_CATEGORIES.length};

        for (String cat : FEATURED_CATEGORIES) {
            ApiClient.getApiService().getMealsByCategory(cat).enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                        List<Meal> catMeals = response.body().getMeals();
                        synchronized (combined) {
                            // Take first meal from each category for diversity
                            if (!catMeals.isEmpty()) combined.add(catMeals.get(0));
                        }
                    }
                    synchronized (remaining) {
                        remaining[0]--;
                        if (remaining[0] == 0) finishDiverseLoad(combined);
                    }
                }
                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    synchronized (remaining) {
                        remaining[0]--;
                        if (remaining[0] == 0) finishDiverseLoad(combined);
                    }
                }
            });
        }
    }

    private void finishDiverseLoad(List<Meal> combined) {
        runOnUiThread(() -> {
            if (combined.isEmpty()) {
                showFeaturedError(getString(R.string.error_loading_data));
            } else {
                List<Meal> display = combined.size() > 6 ? combined.subList(0, 6) : combined;
                featuredAdapter.setMeals(display);
                featuredAdapter.setTag("Popular");
                showFeaturedContent();
            }
        });
    }

    private void loadCategories() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showCategoriesError(getString(R.string.error_no_internet));
            return;
        }
        showCategoriesLoading();
        ApiClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getCategories();
                    categoriesAdapter.setCategories(categories);
                    showCategoriesContent();
                } else {
                    showCategoriesError(getString(R.string.error_loading_data));
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showCategoriesError(getString(R.string.error_loading_data));
            }
        });
    }

    private String getDisplayLabel(String filter) {
        switch (filter) {
            case FILTER_VEGAN: return "Vegan";
            case FILTER_VEGETARIAN: return "Vegetarian";
            default: return "Popular";
        }
    }

    private void openRecipeDetail(String mealId) {
        Intent intent = new Intent(this, com.example.mealapplication.ui.detail.RecipeDetailActivity.class);
        intent.putExtra(com.example.mealapplication.ui.detail.RecipeDetailActivity.EXTRA_MEAL_ID, mealId);
        startActivity(intent);
    }

    private void showFeaturedLoading() {
        featuredProgressBar.setVisibility(View.VISIBLE);
        featuredRecyclerView.setVisibility(View.GONE);
        featuredErrorLayout.setVisibility(View.GONE);
    }
