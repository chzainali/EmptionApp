package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.emotionapp.SharedPreferencesHelper;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.CategoriesDetailsAdapter;
import com.example.emotionapp.databinding.FragmentCategoriesDetailsBinding;
import com.example.emotionapp.jsonreader.Category;
import com.example.emotionapp.jsonreader.MotivationalQuotes;
import com.example.emotionapp.jsonreader.MotivationalQuotesLoader;
import com.example.emotionapp.models.OnItemClick;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class CategoriesDetailsFragment extends Fragment {

    FragmentCategoriesDetailsBinding binding;
    String category;

    List<String> list = new ArrayList<>();
    MainActivity activity;

    public CategoriesDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("data");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (MainActivity) requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();

        MotivationalQuotes motivationalQuotes = MotivationalQuotesLoader.loadMotivationalQuotes(requireContext());
        for (Map.Entry<String, Category> category : motivationalQuotes.getMotivationalQuotes().entrySet()) {
            if (Objects.equals(category.getKey(), this.category)) {
                list.addAll(Arrays.asList(category.getValue().getQuotes()));
            }
        }
        binding.tvName.setText(category);
        binding.rvData.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvData.setAdapter(new CategoriesDetailsAdapter(list, requireContext(), new OnItemClick() {
            @Override
            public void onClick(int position) {

            }
        }));
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(CategoriesDetailsFragment.this).navigateUp();
            }
        });
    }
}