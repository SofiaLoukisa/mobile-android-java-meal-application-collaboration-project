package com.example.mealapplication.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_meals")
public class FavoriteMealEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String thumbnail;

    public FavoriteMealEntity(@NonNull String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbnail() { return thumbnail; }
}