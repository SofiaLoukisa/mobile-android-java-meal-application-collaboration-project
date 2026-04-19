package com.example.mealapplication.remote;

import com.example.mealapplication.model.CategoryResponse;
import com.example.mealapplication.model.MealDetailResponse;
import com.example.mealapplication.model.MealResponse;

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

    @GET("filter.php")
    Call<MealResponse> filterByArea(@Query("a") String area);

    @GET("lookup.php")
    Call<MealDetailResponse> getMealDetails(@Query("i") String mealId);
}