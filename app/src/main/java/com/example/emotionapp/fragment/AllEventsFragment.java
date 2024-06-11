package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.R;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.EventsAdapter;
import com.example.emotionapp.databinding.FragmentAllEventsBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.OnItemClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllEventsFragment extends Fragment {
    FragmentAllEventsBinding binding;
    List<EventsDto> list = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EventsAdapter adapter;
    ProgressDialog progressDialog;
    MainActivity activity;

    public AllEventsFragment() {
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
        binding = FragmentAllEventsBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.topBar.tvTitle.setText("Timeline");


        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);

        progressDialog.show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("events");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        EventsDto model = snapshot1.getValue(EventsDto.class);
                        list.add(model);
                    }
                    Collections.sort(list, Comparator.comparingLong(EventsDto::getTime));
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, "No Data to Show", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new EventsAdapter(list, activity, new OnItemClick() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", list.get(position));
                findNavController(AllEventsFragment.this).navigate(R.id.action_allEventsFragment_to_eventDetailsFragment, bundle);
            }
        });
        binding.rvEvent.setAdapter(adapter);
        binding.topBar.btnDone.setVisibility(View.GONE);
        binding.topBar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(AllEventsFragment.this).navigateUp();
            }
        });


    }
}