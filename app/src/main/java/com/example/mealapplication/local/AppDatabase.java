package com.example.mealapplication.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavoriteMealEntity.class, MealPlanEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FavoriteMealDao favoriteMealDao();
    public abstract MealPlanDao mealPlanDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "meal_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}