package com.example.emotionapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ItemAddImageBinding;
import com.example.emotionapp.databinding.ItemImagesBinding;
import com.example.emotionapp.models.OnItemClick;

import java.util.List;

public class AddImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> list;
    Context context;
    OnItemClick onItemClick;
    boolean isAdding = false;

    public AddImagesAdapter(List<String> list, Context context, OnItemClick onItemClick) {
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
        isAdding = list.contains(null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_image, parent, false);
            return new AddImagesAdapter.Vh(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
            return new AddImagesAdapter.AddImageVh(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClick(position);
                }
            });
        } else {

            AddImageVh vh = (AddImageVh) holder;
            if (!isAdding) {
                ((AddImageVh) holder).binding.imgDelete.setVisibility(View.GONE);
            }
            Glide.with(context).load(list.get(position)).into(vh.binding.img);

            ((AddImageVh) holder).binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClick(position);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        ItemAddImageBinding binding;

        public Vh(@NonNull View itemView) {
            super(itemView);
            binding = ItemAddImageBinding.bind(itemView);
        }
    }

    public static class AddImageVh extends RecyclerView.ViewHolder {
        ItemImagesBinding binding;

        public AddImageVh(@NonNull View itemView) {
            super(itemView);
            binding = ItemImagesBinding.bind(itemView);
        }
    }
}
