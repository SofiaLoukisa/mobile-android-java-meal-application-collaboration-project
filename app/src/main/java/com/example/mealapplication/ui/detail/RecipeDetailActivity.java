package com.example.mealapplication.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.mealapplication.R;
import com.example.mealapplication.local.AppDatabase;
import com.example.mealapplication.local.FavoriteMealEntity;
import com.example.mealapplication.local.MealPlanEntity;
import com.example.mealapplication.model.MealDetail;
import com.example.mealapplication.model.MealDetailResponse;
import com.example.mealapplication.remote.ApiClient;
import com.example.mealapplication.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEAL_ID = "extra_meal_id";

    private String mealId;
    private MealDetail currentMeal;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ImageView recipeImage;
    private TextView recipeName, ingredientsList, categoryChip, areaChip;
    private LinearLayout instructionsContainer;
    private CircularProgressIndicator progressBar;
    private View scrollView;
    private View errorLayout;
    private TextView errorTextView;
    private MaterialButton favoriteButton;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_recipe_detail);

        mealId = getIntent().getStringExtra(EXTRA_MEAL_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recipeImage = findViewById(R.id.recipeImage);
        recipeName = findViewById(R.id.recipeName);
        ingredientsList = findViewById(R.id.ingredientsList);
        categoryChip = findViewById(R.id.categoryChip);
        areaChip = findViewById(R.id.areaChip);
        instructionsContainer = findViewById(R.id.instructionsContainer);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        errorLayout = findViewById(R.id.errorLayout);
        errorTextView = findViewById(R.id.errorTextView);
        favoriteButton = findViewById(R.id.favoriteButton);

        favoriteButton.setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.shareButton).setOnClickListener(v -> shareRecipe());
        findViewById(R.id.addToPlanButton).setOnClickListener(v -> showDaySelectionDialog());
        findViewById(R.id.retryButton).setOnClickListener(v -> loadMealDetails());

        loadMealDetails();
    }

    private void loadMealDetails() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError(getString(R.string.error_no_internet));
            return;
        }
        showLoading();
        ApiClient.getApiService().getMealDetails(mealId).enqueue(new Callback<MealDetailResponse>() {
            @Override
            public void onResponse(Call<MealDetailResponse> call, Response<MealDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    currentMeal = response.body().getMeals().get(0);
                    showContent(currentMeal);
                    checkIfFavorite();
                } else {
                    showError(getString(R.string.error_loading_data));
                }
            }

            @Override
            public void onFailure(Call<MealDetailResponse> call, Throwable t) {
                showError(getString(R.string.error_loading_data));
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        if (errorLayout != null) errorLayout.setVisibility(View.GONE);
    }

    private void showContent(MealDetail meal) {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        if (errorLayout != null) errorLayout.setVisibility(View.GONE);

        recipeName.setText(meal.getName());
        categoryChip.setText(meal.getCategory());
        areaChip.setText(meal.getArea());

        Glide.with(this).load(meal.getThumbnail()).into(recipeImage);

        // Render ingredients with green bullet
        buildIngredientsList(meal);

        // Render numbered instruction steps
        buildInstructionSteps(meal.getInstructions());
    }

    private void buildIngredientsList(MealDetail meal) {
        int green = ContextCompat.getColor(this, R.color.primary);
        StringBuilder sb = new StringBuilder();
        for (String ingredient : meal.getIngredients()) {
            sb.append("● ").append(ingredient).append("\n");
        }
        String text = sb.toString().trim();
        SpannableString spannable = new SpannableString(text);
        int start = 0;
        while (start < text.length()) {
            int bulletEnd = text.indexOf(' ', start);
            if (bulletEnd == -1) break;
            if (text.charAt(start) == '●') {
                spannable.setSpan(new ForegroundColorSpan(green), start, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            int nextLine = text.indexOf('\n', start);
            start = (nextLine == -1) ? text.length() : nextLine + 1;
        }
        ingredientsList.setText(spannable);
    }
