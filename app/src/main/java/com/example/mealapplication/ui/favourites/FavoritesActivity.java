package com.example.mealapplication.ui.favourites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.local.AppDatabase;
import com.example.mealapplication.local.FavoriteMealEntity;
import com.example.mealapplication.ui.browse.BrowseActivity;
import com.example.mealapplication.ui.detail.RecipeDetailActivity;
import com.example.mealapplication.ui.meals.MealListActivity;
import com.example.mealapplication.ui.planner.MealPlanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private View emptyFavoritesLayout;
    private BottomNavigationView bottomNav;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView);
        emptyFavoritesLayout = findViewById(R.id.emptyFavoritesLayout);

        android.view.View accountButton = findViewById(R.id.accountButton);
        if (accountButton != null) {
            accountButton.setOnClickListener(v -> {
                startActivity(new Intent(this, com.example.mealapplication.ui.profile.ProfileActivity.class));
            });
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FavoritesAdapter(new FavoritesAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(FavoriteMealEntity meal) {
                Intent intent = new Intent(FavoritesActivity.this, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.getId());
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(FavoriteMealEntity meal) {
                removeFavorite(meal);
            }
        });
        recyclerView.setAdapter(adapter);

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_favorites) return true;
            Intent intent;
            if (id == R.id.nav_home) {
                intent = new Intent(this, BrowseActivity.class);
            } else if (id == R.id.nav_recipes) {
                intent = new Intent(this, MealListActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_favorites);
        loadFavorites();
    }

    private void loadFavorites() {
        executorService.execute(() -> {
            List<FavoriteMealEntity> favorites = AppDatabase.getInstance(this).favoriteMealDao().getAllFavorites();
            runOnUiThread(() -> {
                if (favorites.isEmpty()) {
                    emptyFavoritesLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyFavoritesLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setFavorites(favorites);
                }
            });
        });
    }

    private void removeFavorite(FavoriteMealEntity meal) {
        executorService.execute(() -> {
            AppDatabase.getInstance(this).favoriteMealDao().delete(meal);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
                loadFavorites();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
