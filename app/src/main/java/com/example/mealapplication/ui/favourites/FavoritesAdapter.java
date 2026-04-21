package com.example.mealapplication.ui.favourites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealapplication.R;
import com.example.mealapplication.local.FavoriteMealEntity;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<FavoriteMealEntity> favorites = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteMealEntity meal);
        void onRemoveClick(FavoriteMealEntity meal);
    }

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void setFavorites(List<FavoriteMealEntity> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteMealEntity meal = favorites.get(position);
        holder.nameTextView.setText(meal.getName());
        Glide.with(holder.itemView.getContext())
                .load(meal.getThumbnail())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> listener.onFavoriteClick(meal));
        holder.removeButton.setOnClickListener(v -> listener.onRemoveClick(meal));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        View removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mealImage);
            nameTextView = itemView.findViewById(R.id.mealName);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
