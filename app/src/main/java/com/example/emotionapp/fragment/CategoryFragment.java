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

import com.example.emotionapp.R;
import com.example.emotionapp.SharedPreferencesHelper;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.CategoriesAdapter;
import com.example.emotionapp.adapter.CategoriesDetailsAdapter;
import com.example.emotionapp.databinding.FragmentCategoryBinding;
import com.example.emotionapp.jsonreader.Category;
import com.example.emotionapp.jsonreader.MotivationalQuotes;
import com.example.emotionapp.jsonreader.MotivationalQuotesLoader;
import com.example.emotionapp.models.OnItemClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Random;


public class CategoryFragment extends Fragment {
    FragmentCategoryBinding binding;
    List<String> list;
    List<String> listSingle;
    DatabaseReference databaseReference;
    Map<String, Object> hashMap;
    MainActivity activity;
    String currentDate;
    String savedQuote;
    SharedPreferencesHelper sharedPreferencesHelper;
    boolean shouldChange;
    HashMap<String, List<String>> hashMapSingle;


    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>();
        listSingle = new ArrayList<>();
        hashMap = new HashMap<>();
        hashMapSingle = new HashMap<>();

        sharedPreferencesHelper = new SharedPreferencesHelper(activity);
        currentDate = Utils.formatDateMonth(System.currentTimeMillis());

        savedQuote = sharedPreferencesHelper.getQuote();
        shouldChange = sharedPreferencesHelper.getSavedTime().equals(currentDate);

        databaseReference = FirebaseDatabase.getInstance().getReference("quotes");
        MotivationalQuotes quotes = MotivationalQuotesLoader.loadMotivationalQuotes(activity);
        for (Map.Entry<String, Category> category : quotes.getMotivationalQuotes().entrySet()) {
            list.add(category.getKey());
            hashMap.put(category.getKey(), Arrays.asList(category.getValue().getQuotes()));
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {
                        };

                        ArrayList<String> list1 = snapshot1.getValue(genericTypeIndicator);
                        Log.e("TAG", "onDataChange: " + list1);
                        hashMapSingle.put(snapshot1.getKey(), list1);
                    }
                    if (shouldChange) {
                        if (!savedQuote.isEmpty()) {
                            listSingle.addAll(hashMapSingle.get(savedQuote));
                        } else {
                            randomData();
                        }
                    } else {
                        randomData();
                    }
                    binding.rvData.setLayoutManager(new LinearLayoutManager(activity));
                    binding.rvData.setAdapter(new CategoriesDetailsAdapter(listSingle, activity, new OnItemClick() {
                        @Override
                        public void onClick(int position) {

                        }
                    }));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.tvDate.setAdapter(new CategoriesAdapter(list, activity, new OnItemClick() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("data", list.get(position));
                findNavController(CategoryFragment.this)
                        .navigate(R.id.action_categoryFragment_to_categoriesDetailsFragment, bundle);
            }
        }));

    }

    private void randomData() {
        List<String> keys = new ArrayList<>(hashMapSingle.keySet());

        Random random = new Random();
        int randomKeyIndex = random.nextInt(hashMapSingle.size());
        String randomKey = keys.get(randomKeyIndex);
        listSingle.addAll(hashMapSingle.get(randomKey));
        sharedPreferencesHelper.setQuote(randomKey);
        sharedPreferencesHelper.setDate(currentDate);
    }



}