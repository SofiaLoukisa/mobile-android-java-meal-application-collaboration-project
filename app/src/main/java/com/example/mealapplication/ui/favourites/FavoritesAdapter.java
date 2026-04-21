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


