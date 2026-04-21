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

    public void setMealPlans(List<MealPlanEntity> plans) {
        this.mealPlans.clear();
        for (MealPlanEntity plan : plans) {
            Map<String, MealPlanEntity> dayMap = mealPlans.get(plan.getDayOfWeek());
            if (dayMap == null) {
                dayMap = new HashMap<>();
                mealPlans.put(plan.getDayOfWeek(), dayMap);
            }
            dayMap.put(plan.getMealSlot(), plan);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days[position];
        holder.dayTextView.setText(day);

        Map<String, MealPlanEntity> dayPlans = mealPlans.get(day);

        setupSlot(holder.slotBreakfast, day, "Breakfast", dayPlans);
        setupSlot(holder.slotSnack, day, "Snack", dayPlans);
        setupSlot(holder.slotLunch, day, "Lunch", dayPlans);
        setupSlot(holder.slotEveningSnack, day, "Evening Snack", dayPlans);
        setupSlot(holder.slotDinner, day, "Dinner", dayPlans);
    }
