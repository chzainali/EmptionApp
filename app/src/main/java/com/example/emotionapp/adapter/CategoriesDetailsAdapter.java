package com.example.emotionapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ItemQuotesBinding;
import com.example.emotionapp.models.OnItemClick;

import java.util.List;

public class CategoriesDetailsAdapter extends RecyclerView.Adapter<CategoriesDetailsAdapter.Vh> {
    List<String> list;
    Context context;
    OnItemClick onItemClick;

    public CategoriesDetailsAdapter(List<String> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quotes, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesDetailsAdapter.Vh holder, int position) {
        String model = list.get(position);
        holder.binding.tvTitle.setText(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemQuotesBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemQuotesBinding.bind(itemView);
        }
    }

}