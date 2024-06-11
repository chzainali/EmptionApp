package com.example.emotionapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ActivityMainBinding;
import com.example.emotionapp.models.OnTimeChange;
import com.example.emotionapp.models.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements OnTimeChange {
    BottomNavigationView bottomNavigationView;
    NavHostFragment navHostFragment;
    ActivityMainBinding binding;
    private SharedViewModel viewModel;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        time=System.currentTimeMillis();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_vendor);
        assert navHostFragment != null;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_vendor);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
        viewModel.getValue().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.e("onChanged", "onChanged: "+aLong );
                time = aLong;
            }
        });
    }


    @Override
    public void OnChange(Long time) {

    }
}