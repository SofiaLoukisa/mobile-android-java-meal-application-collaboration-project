package com.example.mealapplication.ui.browse;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.model.Category;
import com.example.mealapplication.model.CategoryResponse;
import com.example.mealapplication.remote.ApiClient;
import com.example.mealapplication.ui.meals.MealListActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);

        categoriesAdapter = new CategoryAdapter(category -> {
            Intent intent = new Intent(BrowseActivity.this, MealListActivity.class);
            intent.putExtra(MealListActivity.EXTRA_CATEGORY, category.getName());
            intent.putExtra(MealListActivity.EXTRA_TITLE, category.getName());
            startActivity(intent);
        });

        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        loadCategories();
    }

    private void loadCategories() {
        ApiClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getCategories();
                    categoriesAdapter.setCategories(categories);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
            }
        });
    }
}