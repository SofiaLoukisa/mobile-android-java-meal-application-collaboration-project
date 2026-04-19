package com.example.mealapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MealDetail {
    @SerializedName("idMeal")
    private String id;
    @SerializedName("strMeal")
    private String name;
    @SerializedName("strCategory")
    private String category;
    @SerializedName("strArea")
    private String area;
    @SerializedName("strInstructions")
    private String instructions;
    @SerializedName("strMealThumb")
    private String thumbnail;
    @SerializedName("strYoutube")
    private String youtubeUrl;
    @SerializedName("strSource")
    private String sourceUrl;

    @SerializedName("strIngredient1") private String strIngredient1;
    @SerializedName("strIngredient2") private String strIngredient2;
    @SerializedName("strIngredient3") private String strIngredient3;
    @SerializedName("strIngredient4") private String strIngredient4;
    @SerializedName("strIngredient5") private String strIngredient5;
    @SerializedName("strIngredient6") private String strIngredient6;
    @SerializedName("strIngredient7") private String strIngredient7;
    @SerializedName("strIngredient8") private String strIngredient8;
    @SerializedName("strIngredient9") private String strIngredient9;
    @SerializedName("strIngredient10") private String strIngredient10;
    @SerializedName("strIngredient11") private String strIngredient11;
    @SerializedName("strIngredient12") private String strIngredient12;
    @SerializedName("strIngredient13") private String strIngredient13;
    @SerializedName("strIngredient14") private String strIngredient14;
    @SerializedName("strIngredient15") private String strIngredient15;
    @SerializedName("strIngredient16") private String strIngredient16;
    @SerializedName("strIngredient17") private String strIngredient17;
    @SerializedName("strIngredient18") private String strIngredient18;
    @SerializedName("strIngredient19") private String strIngredient19;
    @SerializedName("strIngredient20") private String strIngredient20;

    @SerializedName("strMeasure1") private String strMeasure1;
    @SerializedName("strMeasure2") private String strMeasure2;
    @SerializedName("strMeasure3") private String strMeasure3;
    @SerializedName("strMeasure4") private String strMeasure4;
    @SerializedName("strMeasure5") private String strMeasure5;
    @SerializedName("strMeasure6") private String strMeasure6;
    @SerializedName("strMeasure7") private String strMeasure7;
    @SerializedName("strMeasure8") private String strMeasure8;
    @SerializedName("strMeasure9") private String strMeasure9;
    @SerializedName("strMeasure10") private String strMeasure10;
    @SerializedName("strMeasure11") private String strMeasure11;
    @SerializedName("strMeasure12") private String strMeasure12;
    @SerializedName("strMeasure13") private String strMeasure13;
    @SerializedName("strMeasure14") private String strMeasure14;
    @SerializedName("strMeasure15") private String strMeasure15;
    @SerializedName("strMeasure16") private String strMeasure16;
    @SerializedName("strMeasure17") private String strMeasure17;
    @SerializedName("strMeasure18") private String strMeasure18;
    @SerializedName("strMeasure19") private String strMeasure19;
    @SerializedName("strMeasure20") private String strMeasure20;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getArea() { return area; }
    public String getInstructions() { return instructions; }
    public String getThumbnail() { return thumbnail; }
    public String getYoutubeUrl() { return youtubeUrl; }
    public String getSourceUrl() { return sourceUrl; }

    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        addIngredient(ingredients, strIngredient1, strMeasure1);
        addIngredient(ingredients, strIngredient2, strMeasure2);
        addIngredient(ingredients, strIngredient3, strMeasure3);
        addIngredient(ingredients, strIngredient4, strMeasure4);
        addIngredient(ingredients, strIngredient5, strMeasure5);
        addIngredient(ingredients, strIngredient6, strMeasure6);
        addIngredient(ingredients, strIngredient7, strMeasure7);
        addIngredient(ingredients, strIngredient8, strMeasure8);
        addIngredient(ingredients, strIngredient9, strMeasure9);
        addIngredient(ingredients, strIngredient10, strMeasure10);
        addIngredient(ingredients, strIngredient11, strMeasure11);
        addIngredient(ingredients, strIngredient12, strMeasure12);
        addIngredient(ingredients, strIngredient13, strMeasure13);
        addIngredient(ingredients, strIngredient14, strMeasure14);
        addIngredient(ingredients, strIngredient15, strMeasure15);
        addIngredient(ingredients, strIngredient16, strMeasure16);
        addIngredient(ingredients, strIngredient17, strMeasure17);
        addIngredient(ingredients, strIngredient18, strMeasure18);
        addIngredient(ingredients, strIngredient19, strMeasure19);
        addIngredient(ingredients, strIngredient20, strMeasure20);
        return ingredients;
    }

    private void addIngredient(List<String> list, String ingredient, String measure) {
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            list.add((measure != null ? measure.trim() + " " : "") + ingredient.trim());
        }
    }
}