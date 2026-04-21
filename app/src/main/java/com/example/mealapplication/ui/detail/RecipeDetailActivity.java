package com.example.mealapplication.ui.detail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealapplication.R;
//import com.example.mealapplication.model.MealDetailResponse;
//import com.example.mealapplication.remote.ApiClient;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEAL_ID = "meal_id";

    private TextView mealTitle;
    private TextView mealInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mealTitle = findViewById(R.id.mealTitle);
        mealInstructions = findViewById(R.id.mealInstructions);

        String mealId = getIntent().getStringExtra(EXTRA_MEAL_ID);

        if (mealId != null) {
            loadMealDetails(mealId);
        }
    }

