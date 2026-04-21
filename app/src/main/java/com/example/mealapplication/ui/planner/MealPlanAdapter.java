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

    private void setupSlot(View slotView, String day, String slotName, Map<String, MealPlanEntity> dayPlans) {
        TextView title = slotView.findViewById(R.id.slotTitle);
        View container = slotView.findViewById(R.id.mealContainer);
        TextView emptyText = slotView.findViewById(R.id.emptyStateText);
        ImageView image = slotView.findViewById(R.id.mealImage);
        TextView name = slotView.findViewById(R.id.mealName);
        ImageView clear = slotView.findViewById(R.id.clearButton);

        title.setText(slotName);

        MealPlanEntity plan = (dayPlans != null) ? dayPlans.get(slotName) : null;
        if (plan != null) {
            container.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            name.setText(plan.getMealName());
            Glide.with(slotView.getContext()).load(plan.getMealThumbnail()).into(image);
            container.setOnClickListener(v -> listener.onMealClick(plan.getMealId()));
            clear.setOnClickListener(v -> listener.onClearClick(day, slotName));
        } else {
            container.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return days.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        View slotBreakfast, slotSnack, slotLunch, slotEveningSnack, slotDinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayOfWeek);
            slotBreakfast = itemView.findViewById(R.id.slotBreakfast);
            slotSnack = itemView.findViewById(R.id.slotSnack);
            slotLunch = itemView.findViewById(R.id.slotLunch);
            slotEveningSnack = itemView.findViewById(R.id.slotEveningSnack);
            slotDinner = itemView.findViewById(R.id.slotDinner);
        }
    }
}
