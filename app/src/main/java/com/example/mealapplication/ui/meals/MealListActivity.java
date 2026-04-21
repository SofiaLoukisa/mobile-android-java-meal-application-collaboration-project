package com.example.mealapplication.ui.meals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.adapter.MealAdapter;
import com.example.mealapplication.model.Category;
import com.example.mealapplication.model.CategoryResponse;
import com.example.mealapplication.model.Meal;
import com.example.mealapplication.model.MealResponse;
import com.example.mealapplication.remote.ApiClient;
import com.example.mealapplication.ui.browse.BrowseActivity;
import com.example.mealapplication.ui.detail.RecipeDetailActivity;
import com.example.mealapplication.ui.favourites.FavoritesActivity;
import com.example.mealapplication.ui.planner.MealPlanActivity;
import com.example.mealapplication.utils.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealListActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_SEARCH_QUERY = "extra_search_query";
    public static final String EXTRA_TITLE = "extra_title";

    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private CircularProgressIndicator progressBar;
    private View errorLayout;
    private TextView errorTextView;
    private View emptyLayout;
    private BottomNavigationView bottomNav;

    private String category;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorLayout = findViewById(R.id.errorLayout);
        errorTextView = findViewById(R.id.errorTextView);
        emptyLayout = findViewById(R.id.emptyFilterLayout);

        category = getIntent().getStringExtra(EXTRA_CATEGORY);
        searchQuery = getIntent().getStringExtra(EXTRA_SEARCH_QUERY);
        String customTitle = getIntent().getStringExtra(EXTRA_TITLE);

        if (customTitle != null) {
            setTitle(customTitle);
        } else if (category != null) {
            setTitle(category);
        } else if (searchQuery != null) {
            setTitle(getString(R.string.search_results, searchQuery));
        } else {
            setTitle(getString(R.string.all_recipes));
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MealAdapter(meal -> {
            Intent intent = new Intent(MealListActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.getId());
            startActivity(intent);
        });
        if (category != null) {
            adapter.setTag(category);
        }
        recyclerView.setAdapter(adapter);

        findViewById(R.id.retryButton).setOnClickListener(v -> loadMeals());

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_recipes) return true;
            Intent intent;
            if (id == R.id.nav_home) {
                intent = new Intent(this, BrowseActivity.class);
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

        loadMeals();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_recipes);
    }

    private void loadMeals() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError(getString(R.string.error_no_internet));
            return;
        }
        showLoading();

        if (category != null) {
            // Load by category
            ApiClient.getApiService().getMealsByCategory(category).enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                        showContent(response.body().getMeals());
                    } else {
                        showEmpty();
                    }
                }
                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    showError(getString(R.string.error_loading_data));
                }
            });
        } else if (searchQuery != null) {
            // Try name search first, then area, then category
            searchByName(searchQuery);
        } else {
            // Load all recipes from all categories
            loadAllRecipes();
        }
    }

    /** Search by name first; if no results, try area; if no results, try category */
    private void searchByName(String query) {
        ApiClient.getApiService().searchMealsByName(query).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    showContent(response.body().getMeals());
                } else {
                    // No name results — try area
                    searchByArea(query);
                }
            }
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                showError(getString(R.string.error_loading_data));
            }
        });
    }

    private void searchByArea(String query) {
        ApiClient.getApiService().filterByArea(query).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    showContent(response.body().getMeals());
                } else {
                    // No area results — try category
                    searchByCategory(query);
                }
            }
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                showError(getString(R.string.error_loading_data));
            }
        });
    }

    private void searchByCategory(String query) {
        ApiClient.getApiService().getMealsByCategory(query).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    showContent(response.body().getMeals());
                } else {
                    showEmpty();
                }
            }
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                showError(getString(R.string.error_loading_data));
            }
        });
    }

    /** Load ALL recipes by fetching every category, then all meals per category */
    private void loadAllRecipes() {
        ApiClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCategories() != null) {
                    fetchMealsForCategories(response.body().getCategories());
                } else {
                    showError(getString(R.string.error_loading_data));
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showError(getString(R.string.error_loading_data));
            }
        });
    }

    private void fetchMealsForCategories(List<Category> categories) {
        final List<Meal> allMeals = new ArrayList<>();
        final int[] remaining = {categories.size()};

        for (Category cat : categories) {
            ApiClient.getApiService().getMealsByCategory(cat.getName()).enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                        synchronized (allMeals) {
                            allMeals.addAll(response.body().getMeals());
                        }
                    }
                    synchronized (remaining) {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            runOnUiThread(() -> {
                                if (allMeals.isEmpty()) {
                                    showEmpty();
                                } else {
                                    showContent(allMeals);
                                }
                            });
                        }
                    }
                }
                @Override
                public void onFailure(Call<MealResponse> call, Throwable t) {
                    synchronized (remaining) {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            runOnUiThread(() -> {
                                if (allMeals.isEmpty()) {
                                    showError(getString(R.string.error_loading_data));
                                } else {
                                    showContent(allMeals);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

