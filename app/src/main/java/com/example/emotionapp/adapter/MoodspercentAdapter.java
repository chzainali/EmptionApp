package com.example.emotionapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ItemMoodsPercentBinding;
import com.example.emotionapp.models.MoodsData;
import com.example.emotionapp.models.OnItemClick;

import java.util.List;

public class MoodspercentAdapter extends RecyclerView.Adapter<MoodspercentAdapter.Vh> {
    List<MoodsData> list;
    Context context;
    OnItemClick onItemClick;
    private int selected = -1;

    public MoodspercentAdapter(List<MoodsData> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public MoodspercentAdapter.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moods_percent, parent, false);
        return new MoodspercentAdapter.Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodspercentAdapter.Vh holder, int position) {
        MoodsData model = list.get(position);
        holder.binding.dayIcon.setImageDrawable(model.getIcon());
        holder.binding.tvPercent.setText(model.getPercent());
        holder.binding.view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(model.getColor())));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected != position) {
                    selected = position;
                    notifyDataSetChanged();
                }

                onItemClick.onClick(holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemMoodsPercentBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemMoodsPercentBinding.bind(itemView);
        }
    }
}
