package com.example.mealapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealapplication.R;
import com.example.mealapplication.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private String tag = null;
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public MealAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    /** Optional tag shown as a chip on every card (e.g., "Vegan", "Chicken"). */
    public void setTag(String tag) {
        this.tag = tag;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.nameTextView.setText(meal.getName());
        Glide.with(holder.itemView.getContext())
                .load(meal.getThumbnail())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(holder.imageView);

        if (tag != null && !tag.isEmpty()) {
            holder.tagView.setVisibility(View.VISIBLE);
            holder.tagView.setText(tag);
        } else {
            holder.tagView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView tagView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mealImage);
            nameTextView = itemView.findViewById(R.id.mealName);
            tagView = itemView.findViewById(R.id.mealTag);
        }
    }
}
