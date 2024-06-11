package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.emotionapp.R;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.EventsAdapter;
import com.example.emotionapp.auth.LoginActivity;
import com.example.emotionapp.databinding.FragmentHomeBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.OnItemClick;
import com.example.emotionapp.models.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeFragment extends Fragment {
    List<EventsDto> list = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FragmentHomeBinding binding;
    EventsAdapter adapter;
    ProgressDialog progressDialog;
    List<EventDay> eventDays = new ArrayList<>();
    private SharedViewModel viewModel;
    MainActivity activity;
    

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(activity).get(SharedViewModel.class);


        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);
        binding.tvTitle.setText(Utils.formatDate(System.currentTimeMillis()));

        progressDialog.show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("events");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                eventDays.clear();
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        EventsDto model = snapshot1.getValue(EventsDto.class);
                        setMood(model);
                        list.add(model);
                    }
                    String date = Utils.formatDate(System.currentTimeMillis());
                    updateData(date);
                } else {
                    String date = Utils.formatDate(System.currentTimeMillis());
                    updateData(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        firebaseDatabase.getReference("Users").child(id).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long coins = 0L;
                if (snapshot.exists()) {
                    coins = (Long) snapshot.getValue();
                }
                binding.tvCoins.setText(coins + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.calendarView.setCalendarDayLayout(R.layout.item_day);
        adapter = new EventsAdapter(list, activity, new OnItemClick() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", adapter.getList().get(position));
                findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_eventDetailsFragment, bundle);
            }
        });
        binding.rvEvent.setAdapter(adapter);

        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                long selectedMillis = eventDay.getCalendar().getTimeInMillis();
                viewModel.setValue(selectedMillis);
                String date = Utils.formatDate(selectedMillis);
                binding.tvTitle.setText(date);
                updateData(date);
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        });
    }

    private void setMood(EventsDto eventsDto) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(eventsDto.getTime());
        eventDays.add(new EventDay(calendar, getIcon(eventsDto.getMood())));
    }

    private Drawable getIcon(int mood) {
        switch (mood) {
            case 0:
                return ContextCompat.getDrawable(activity, R.drawable.happy);
            case 1:
                return ContextCompat.getDrawable(activity, R.drawable.smile);
            case 2:
                return ContextCompat.getDrawable(activity, R.drawable.neutral);
            case 3:
                return ContextCompat.getDrawable(activity, R.drawable.sad);
            case 4:
                return ContextCompat.getDrawable(activity, R.drawable.angry);
            default:
                return ContextCompat.getDrawable(activity, R.drawable.neutral);
        }
    }

    private void updateData(String date) {
        List<EventsDto> filteredList = new ArrayList<>();
        for (EventsDto eventsDto : list) {
            String eventDate = Utils.formatDate(eventsDto.getTime());
            if (date.equals(eventDate)) {
                filteredList.add(eventsDto);
            }
        }
        adapter.setList(filteredList);
        binding.calendarView.setEvents(eventDays);
        if (filteredList.isEmpty()) {
            binding.tvNoEvent.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoEvent.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}