package com.example.mealapplication.model;

import com.google.gson.annotations.SerializedName;

public class Meal {
    @SerializedName("idMeal")
    private String id;
    @SerializedName("strMeal")
    private String name;
    @SerializedName("strMealThumb")
    private String thumbnail;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
}