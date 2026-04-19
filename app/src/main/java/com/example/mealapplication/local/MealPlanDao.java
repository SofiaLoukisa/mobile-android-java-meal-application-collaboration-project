package com.example.mealapplication.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(MealPlanEntity plan);

    @Query("SELECT * FROM meal_plan")
    List<MealPlanEntity> getAllMealPlans();

    @Query("SELECT * FROM meal_plan WHERE dayOfWeek = :day AND mealSlot = :slot LIMIT 1")
    MealPlanEntity getPlanForSlot(String day, String slot);

    @Query("DELETE FROM meal_plan WHERE dayOfWeek = :day AND mealSlot = :slot")
    void deletePlanForSlot(String day, String slot);

    @Query("DELETE FROM meal_plan")
    void deleteAll();
}