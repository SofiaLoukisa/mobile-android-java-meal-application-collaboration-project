package com.example.mealapplication.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoriteMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteMealEntity meal);

    @Delete
    void delete(FavoriteMealEntity meal);

    @Query("SELECT * FROM favorite_meals")
    List<FavoriteMealEntity> getAllFavorites();

    @Query("SELECT EXISTS(SELECT * FROM favorite_meals WHERE id = :mealId)")
    boolean isFavorite(String mealId);

    @Query("DELETE FROM favorite_meals")
    void deleteAll();
}