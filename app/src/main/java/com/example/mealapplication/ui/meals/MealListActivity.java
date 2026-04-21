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

