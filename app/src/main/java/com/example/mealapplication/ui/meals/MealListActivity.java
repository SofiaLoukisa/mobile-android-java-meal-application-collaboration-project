package com.example.mealapplication.ui.meals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.adapter.MealAdapter;
import com.example.mealapplication.model.Meal;
import com.example.mealapplication.model.MealResponse;
import com.example.mealapplication.remote.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealListActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_SEARCH_QUERY = "search_query";

    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        recyclerView = findViewById(R.id.mealRecyclerView);

        mealAdapter = new MealAdapter(meal -> {
            // αργότερα: open RecipeDetailActivity
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(mealAdapter);

        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        String search = getIntent().getStringExtra(EXTRA_SEARCH_QUERY);

        if (category != null) {
            loadMealsByCategory(category);
        } else if (search != null) {
            searchMeals(search);
        }
    }

    private void loadMealsByCategory(String category) {
        ApiClient.getApiService().getMealsByCategory(category)
                .enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> meals = response.body().getMeals();
                            mealAdapter.setMeals(meals);
                        }
                    }

                    @Override
                    public void onFailure(Call<MealResponse> call, Throwable t) {
                    }
                });
    }

    private void searchMeals(String query) {
        ApiClient.getApiService().searchMealsByName(query)
                .enqueue(new Callback<MealResponse>() {
                    @Override
                    public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> meals = response.body().getMeals();
                            mealAdapter.setMeals(meals);
                        }
                    }

                    @Override
                    public void onFailure(Call<MealResponse> call, Throwable t) {
                    }
                });
    }
}