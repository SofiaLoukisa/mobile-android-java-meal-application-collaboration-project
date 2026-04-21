package com.example.mealapplication.ui.planner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.local.AppDatabase;
import com.example.mealapplication.local.MealPlanEntity;
import com.example.mealapplication.ui.browse.BrowseActivity;
import com.example.mealapplication.ui.detail.RecipeDetailActivity;
import com.example.mealapplication.ui.favourites.FavoritesActivity;
import com.example.mealapplication.ui.meals.MealListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealPlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealPlanAdapter adapter;
    private BottomNavigationView bottomNav;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        android.view.View accountButton = findViewById(R.id.accountButton);
        if (accountButton != null) {
            accountButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.mealapplication.ui.profile.ProfileActivity.class);
                startActivity(intent);
            });
        }

        adapter = new MealPlanAdapter(new MealPlanAdapter.OnMealPlanClickListener() {
            @Override
            public void onMealClick(String mealId) {
                Intent intent = new Intent(MealPlanActivity.this, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, mealId);
                startActivity(intent);
            }

            @Override
            public void onClearClick(String day, String slot) {
                clearPlan(day, slot);
            }
        });
        recyclerView.setAdapter(adapter);

