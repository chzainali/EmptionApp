package com.example.emotionapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ItemMoodsBinding;
import com.example.emotionapp.models.OnItemClick;

import java.util.List;

public class ModesAdapter extends RecyclerView.Adapter<ModesAdapter.Vh> {
    List<Integer> list;
    Context context;
    OnItemClick onItemClick;
    private int selected = -1;

    public ModesAdapter(List<Integer> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ModesAdapter.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moods, parent, false);
        return new ModesAdapter.Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModesAdapter.Vh holder, int position) {
        Integer model = list.get(position);
        holder.binding.dayIcon.setImageResource(model);
        if (selected == position) {
            holder.binding.checked.setVisibility(View.VISIBLE);
        } else {
            holder.binding.checked.setVisibility(View.GONE);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected != position) {
                    selected = position;
                    notifyDataSetChanged();
                }

                onItemClick.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemMoodsBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemMoodsBinding.bind(itemView);
        }
    }
}
