package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.CustomYAxisRenderer;
import com.example.emotionapp.R;
import com.example.emotionapp.SegmentedProgressBar;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.EventsAdapter;
import com.example.emotionapp.adapter.MoodspercentAdapter;
import com.example.emotionapp.databinding.FragmentBoardBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.MoodsData;
import com.example.emotionapp.models.OnItemClick;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BoardFragment extends Fragment {
    private LineChart lineChart;
    private FragmentBoardBinding binding;
    List<EventsDto> list = new ArrayList<>();
    List<MoodsData> moodsList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EventsAdapter adapter;
    MoodspercentAdapter modesAdapter;
    ProgressDialog progressDialog;
    MainActivity activity;

    public BoardFragment() {
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
        binding = FragmentBoardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = binding.lineChart;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);

        progressDialog.show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("events");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.orderByChild("userId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        EventsDto model = snapshot1.getValue(EventsDto.class);
                        list.add(model);
                    }
                    if (list.size() > 0){
                        setLineChart();
                        adapter.notifyDataSetChanged();
                        setMoods();
                    }
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
                findNavController(BoardFragment.this).navigate(R.id.action_boardFragment_to_eventDetailsFragment, bundle);
            }
        });

        binding.tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(BoardFragment.this).navigate(R.id.action_boardFragment_to_allEventsFragment);
            }
        });

        binding.rvEvent.setAdapter(adapter);
    }


    private class MyXAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            // Convert float value to integer
            int index = (int) value;
            Date date = new Date((long) value);
            // Format the Date object using SimpleDateFormat
            return mFormat.format(date);
        }
    }

    private void setLineChart() {
        List<Entry> entries = new ArrayList<>();
        for (EventsDto eventsDto : list) {
            try {
                float time = eventsDto.getTime();
                float mood = eventsDto.getMood();

                // Check for invalid values
                if (!Float.isNaN(time) && !Float.isNaN(mood)) {
                    entries.add(new Entry(time, mood));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "Moods");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(3);
        dataSet.setDrawValues(false);


        // Customize the appearance of the chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setGridBackgroundColor(Color.TRANSPARENT);

        // Customize X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // Disable horizontal grid lines

        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        Drawable[] icons = new Drawable[5];
        icons[0] = getResources().getDrawable(R.drawable.happy);
        icons[1] = getResources().getDrawable(R.drawable.smile);
        icons[2] = getResources().getDrawable(R.drawable.neutral);
        icons[3] = getResources().getDrawable(R.drawable.sad);
        icons[4] = getResources().getDrawable(R.drawable.angry);

        // Customize Y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawGridLines(false);


        //   yAxisRight.setValueFormatter(new MyYAxisValueFormatter());

        YAxis yAxisLeft = lineChart.getAxisLeft();
        lineChart.getAxisLeft().setLabelCount(5);
        yAxisLeft.setGranularity(1.0f);
        yAxisLeft.setGranularityEnabled(true);
        yAxisLeft.setAxisMaximum(4);
        yAxisLeft.setAxisMinimum(0);
        CustomYAxisRenderer customYAxisRenderer = new CustomYAxisRenderer(lineChart.getViewPortHandler(), yAxisLeft,
                lineChart.getTransformer(YAxis.AxisDependency.LEFT), activity);
        customYAxisRenderer.setIcons(icons);
        lineChart.setRendererLeftYAxis(customYAxisRenderer);


        lineChart.invalidate();
        // Invalidate the chart to refresh it
    }

    private void setMoods() {
        moodsList.clear();

        Map<Integer, Integer> moodCounts = new HashMap<>();
        for (EventsDto obj : list) {
            int mood = obj.getMood();
            if (moodCounts.containsKey(mood)) {
                moodCounts.put(mood, moodCounts.getOrDefault(mood, 0) + 1);
            } else {
                moodCounts.put(mood, 1);
            }
        }
        int totalMoods = list.size();

        Map<Integer, Double> percentageByMood = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : moodCounts.entrySet()) {
            int mood = entry.getKey();
            int count = entry.getValue();
            double percentage = (count / (double) totalMoods) * 100;
            percentageByMood.put(mood, percentage);
        }
        for (int i = 0; i <= 4; i++) {
            if (!percentageByMood.containsKey(i)) {
                percentageByMood.put(i, 0.0);
            }
        }
        List<SegmentedProgressBar.BarContext> list1 = new ArrayList<>();
        // Print the percentage of each mood value
        for (Map.Entry<Integer, Double> entry : percentageByMood.entrySet()) {
            int mood = entry.getKey();
            double percentage = entry.getValue();
            String color = "";
            switch (mood) {
                case 0:
                    color = "#4CAF50";
                    list1.add(new SegmentedProgressBar.BarContext(Color.parseColor("#4CAF50"),
                            Color.parseColor("#4CAF50"), (float) (percentage / 100)));
                    break;
                case 1:
                    color = "#5AE1FF";
                    list1.add(new SegmentedProgressBar.BarContext(Color.parseColor("#49C6FC"),
                            Color.parseColor("#5AE1FF"),
                            (float) (percentage / 100)));
                    break;
                case 2:
                    color = "#C4AEFC";
                    list1.add(new SegmentedProgressBar.BarContext(Color.parseColor("#C4AEFC"),
                            Color.parseColor("#F29DDA"),
                            (float) (percentage / 100)));
                    break;

                case 3:
                    color = "#FFBF74";
                    list1.add(new SegmentedProgressBar.BarContext(Color.parseColor("#FFBF74"),
                            Color.parseColor("#FFDA58"),
                            (float) (percentage / 100)));
                    break;
                case 4:
                    color = "#FF6C5C";
                    list1.add(new SegmentedProgressBar.BarContext(Color.parseColor("#FF6C5C"), //gradient start
                            Color.parseColor("#FF8E95"), //gradient stop
                            (float) (percentage / 100)));
                    break;
            }
            moodsList.add(new MoodsData(Utils.getIcon(activity, mood), percentage + "%", color));

        }


        ;
        binding.segmentProgressBar.setContexts(list1);


        modesAdapter = new MoodspercentAdapter(moodsList, activity, new OnItemClick() {
            @Override
            public void onClick(int position) {
            }
        });
        binding.rvEmotion.setAdapter(modesAdapter);
    }
}