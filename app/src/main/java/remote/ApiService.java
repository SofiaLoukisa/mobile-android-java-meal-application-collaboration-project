package com.example.mealapplication.remote;

import com.example.testmealplanner.model.CategoryResponse;
import com.example.testmealplanner.model.MealDetailResponse;
import com.example.testmealplanner.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String category);

    @GET("search.php")
    Call<MealResponse> searchMealsByName(@Query("s") String query);

    @GET("lookup.php")
    Call<MealDetailResponse> getMealDetails(@Query("i") String mealId);
}