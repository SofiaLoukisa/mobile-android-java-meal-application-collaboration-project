package com.example.mealapplication.ui.planner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealapplication.R;
import com.example.mealapplication.local.MealPlanEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.ViewHolder> {

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private final Map<String, Map<String, MealPlanEntity>> mealPlans = new HashMap<>();
    private final OnMealPlanClickListener listener;

    public interface OnMealPlanClickListener {
        void onMealClick(String mealId);
        void onClearClick(String day, String slot);
    }

    public MealPlanAdapter(OnMealPlanClickListener listener) {
        this.listener = listener;
    }
