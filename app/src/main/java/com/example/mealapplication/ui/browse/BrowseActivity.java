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

