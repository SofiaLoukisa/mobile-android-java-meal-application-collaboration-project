package com.example.mealapplication.remote;

import com.example.mealapplication.model.CategoryResponse;
//import com.example.mealapplication.model.MealDetailResponse;
//import com.example.mealapplication.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("categories.php")
    Call<CategoryResponse> getCategories();

}