package com.example.emotionapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.ImageModel;
import com.example.emotionapp.databinding.ItemEventBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.OnItemClick;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.Vh> {
    List<EventsDto> list;
    Context context;
    OnItemClick onItemClick;

    public EventsAdapter(List<EventsDto> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    public void setList(List<EventsDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<EventsDto> getList() {
        return list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        EventsDto model = list.get(position);
        holder.binding.tvDate.setText(Utils.formatDateMonth(model.getTime()));
        holder.binding.dayIcon.setImageDrawable(Utils.getIcon(context, model.getMood()));
        holder.binding.tvTitle.setText(model.getDetails());
        List<String> imagesList = new ArrayList<>();
        if (model.getImages() != null) {
            for (ImageModel images : model.getImages().values()) {
                imagesList.add(images.getUrl());
            }
            holder.binding.rvImages.setAdapter(new AddImagesAdapter(imagesList, context, new OnItemClick() {
                @Override
                public void onClick(int position) {

                }
            }));
        }
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
        ItemEventBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemEventBinding.bind(itemView);
        }
    }
}
