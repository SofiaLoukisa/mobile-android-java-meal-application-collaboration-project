package com.example.mealapplication.ui.favourites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealapplication.R;
import com.example.mealapplication.adapter.MealAdapter;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);

        mealAdapter = new MealAdapter(meal -> {

        });

        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoritesRecyclerView.setAdapter(mealAdapter);
    }
}