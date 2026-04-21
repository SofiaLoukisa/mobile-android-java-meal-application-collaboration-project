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

    private void buildInstructionSteps(String instructions) {
        instructionsContainer.removeAllViews();
        if (instructions == null || instructions.isEmpty()) return;

        // Split by double newline or single newline, filter empty lines
        String[] rawLines = instructions.split("\\r?\\n");
        int stepNumber = 1;
        for (String line : rawLines) {
            String trimmed = line.trim();
            // Skip very short lines (just numbers or empty)
            if (trimmed.isEmpty() || trimmed.matches("^\\d+\\.?$")) continue;
            // Remove leading step numbers like "1." or "Step 1:"
            String stepText = trimmed.replaceAll("^\\d+[.:]?\\s*", "").trim();
            if (stepText.isEmpty()) continue;

            addStepView(stepNumber, stepText);
            stepNumber++;
        }
    }

    private void addStepView(int number, String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, dpToPx(14));
        row.setLayoutParams(rowParams);

        // Number circle
        TextView numberView = new TextView(this);
        int size = dpToPx(28);
        LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(size, size);
        numParams.setMarginEnd(dpToPx(12));
        numParams.topMargin = dpToPx(2);
        numberView.setLayoutParams(numParams);
        numberView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_step_circle));
        numberView.setText(String.valueOf(number));
        numberView.setTextColor(0xFFFFFFFF);
        numberView.setTextSize(11f);
        numberView.setGravity(Gravity.CENTER);
        numberView.setTypeface(null, android.graphics.Typeface.BOLD);

        // Step text
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(14f);
        textView.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        textView.setLineSpacing(dpToPx(2), 1.0f);

        row.addView(numberView);
        row.addView(textView);
        instructionsContainer.addView(row);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        if (errorLayout != null) {
            errorLayout.setVisibility(View.VISIBLE);
            if (errorTextView != null) errorTextView.setText(message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfFavorite() {
        executorService.execute(() -> {
            isFavorite = AppDatabase.getInstance(this).favoriteMealDao().isFavorite(mealId);
            runOnUiThread(this::updateFavoriteButton);
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            favoriteButton.setText(getString(R.string.btn_remove_favorite));
            favoriteButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_nav_favorites));
            favoriteButton.setIconTint(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.primary)));
        } else {
            favoriteButton.setText(getString(R.string.btn_save_favorite));
            favoriteButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_nav_favorites));
            favoriteButton.setIconTint(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.on_surface_variant)));
        }
    }

    private void toggleFavorite() {
        if (currentMeal == null) return;
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            FavoriteMealEntity entity = new FavoriteMealEntity(mealId, currentMeal.getName(), currentMeal.getThumbnail());
            if (isFavorite) {
                db.favoriteMealDao().delete(entity);
                isFavorite = false;
            } else {
                db.favoriteMealDao().insert(entity);
                isFavorite = true;
            }
            runOnUiThread(() -> {
                updateFavoriteButton();
                favoriteButton.animate().scaleX(1.15f).scaleY(1.15f).setDuration(120)
                        .withEndAction(() -> favoriteButton.animate().scaleX(1f).scaleY(1f).setDuration(120));
                Toast.makeText(this,
                        isFavorite ? R.string.added_to_favorites : R.string.removed_from_favorites,
                        Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void shareRecipe() {
        if (currentMeal == null) return;
        String source = (currentMeal.getSourceUrl() != null && !currentMeal.getSourceUrl().isEmpty())
                ? currentMeal.getSourceUrl() : "https://www.themealdb.com";
        String shareText = getString(R.string.share_body, currentMeal.getName(), source);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject, currentMeal.getName()));
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)));
    }

    private void showDaySelectionDialog() {
        String[] days = {getString(R.string.monday), getString(R.string.tuesday),
                getString(R.string.wednesday), getString(R.string.thursday),
                getString(R.string.friday), getString(R.string.saturday), getString(R.string.sunday)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_day_title)
                .setItems(days, (dialog, which) -> showSlotSelectionDialog(days[which]))
                .show();
    }

    private void showSlotSelectionDialog(String day) {
        String[] slots = {getString(R.string.slot_breakfast), getString(R.string.slot_snack),
                getString(R.string.slot_lunch), getString(R.string.slot_evening_snack),
                getString(R.string.slot_dinner)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_slot_title)
                .setItems(slots, (dialog, which) -> checkSlotOccupied(day, slots[which]))
                .show();
    }

    private void checkSlotOccupied(String day, String slot) {
        executorService.execute(() -> {
            MealPlanEntity existing = AppDatabase.getInstance(this).mealPlanDao().getPlanForSlot(day, slot);
            runOnUiThread(() -> {
                if (existing != null) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.confirm_replace_title)
                            .setMessage(getString(R.string.confirm_replace_message, day, slot))
                            .setPositiveButton(R.string.replace, (d, w) -> saveToPlan(day, slot))
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                } else {
                    saveToPlan(day, slot);
                }
            });
        });
    }

    private void saveToPlan(String day, String slot) {
        executorService.execute(() -> {
            MealPlanEntity plan = new MealPlanEntity(day, slot, mealId, currentMeal.getName(), currentMeal.getThumbnail());
            AppDatabase.getInstance(this).mealPlanDao().insertOrUpdate(plan);
            runOnUiThread(() -> Toast.makeText(this,
                    getString(R.string.planned_for, day, slot), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
