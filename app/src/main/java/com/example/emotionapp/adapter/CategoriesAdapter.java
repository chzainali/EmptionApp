package com.example.emotionapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ItemCategoriesBinding;
import com.example.emotionapp.models.OnItemClick;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.Vh> {
    List<String> list;
    Context context;
    OnItemClick onItemClick;

    public CategoriesAdapter(List<String> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        String model = list.get(position);
        holder.binding.tvTitle.setText(model);
        holder.binding.getRoot().setCardBackgroundColor(getColorForCategory(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemCategoriesBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoriesBinding.bind(itemView);
        }
    }

    private int getColorForCategory(int position) {
        // Set background color based on position
        switch (position % 5) { // You can set different colors based on categories count.
            case 0:
                return Color.parseColor("#FFC107"); // Orange
            case 1:
                return Color.parseColor("#2196F3"); // Blue
            case 2:
                return Color.parseColor("#4CAF50"); // Green
            case 3:
                return Color.RED;
            case 4:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

}