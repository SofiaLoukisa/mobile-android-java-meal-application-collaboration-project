package com.example.mealapplication.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "meal_plan", primaryKeys = {"dayOfWeek", "mealSlot"})
public class MealPlanEntity {
    @NonNull
    private String dayOfWeek; // e.g., "Monday"
    @NonNull
    private String mealSlot;  // Breakfast, Snack, Lunch, Evening Snack, Dinner
    private String mealId;
    private String mealName;
    private String mealThumbnail;

    public MealPlanEntity(@NonNull String dayOfWeek, @NonNull String mealSlot, String mealId, String mealName, String mealThumbnail) {
        this.dayOfWeek = dayOfWeek;
        this.mealSlot = mealSlot;
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealThumbnail = mealThumbnail;
    }

    @NonNull
    public String getDayOfWeek() { return dayOfWeek; }
    @NonNull
    public String getMealSlot() { return mealSlot; }
    public String getMealId() { return mealId; }
    public String getMealName() { return mealName; }
    public String getMealThumbnail() { return mealThumbnail; }
}