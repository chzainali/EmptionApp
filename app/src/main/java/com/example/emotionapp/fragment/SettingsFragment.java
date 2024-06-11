package com.example.emotionapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.emotionapp.R;
import com.example.emotionapp.SharedPreferencesHelper;
import com.example.emotionapp.auth.LoginActivity;
import com.example.emotionapp.auth.RegisterActivity;
import com.example.emotionapp.auth.model.User;
import com.example.emotionapp.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;
    FirebaseDatabase database;
    DatabaseReference db;
    ProgressDialog progressDialog;
    User user;
    SharedPreferencesHelper helper;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        db = database.getReference("Users");
        helper = new SharedPreferencesHelper(requireContext());

        db.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                progressDialog.dismiss();
                if (snapshot1.exists()) {
                    for (DataSnapshot snapshot : snapshot1.getChildren()) {
                        Log.d("SNAPSHOT", "onDataChange: " + snapshot.toString());
                        String firstName = (String) snapshot.child("fName").getValue();
                        String lastName = (String) snapshot.child("lName").getValue();
                        String email = (String) snapshot.child("email").getValue();
                        String imageUri = (String) snapshot.child("imageUri").getValue();
                        String id = (String) snapshot.child("id").getValue();
                        Long coins = (Long) snapshot.child("coins").getValue();
                        if (coins == null) coins = 0L;
                        user = new User(id, firstName, lastName, email, imageUri, coins);
                        binding.tvEmail.setText(user.getEmail());
                        binding.tvFirstName.setText(user.getfName());
                        binding.tvLastName.setText(user.getlName());
                        Glide.with(requireContext()).load(user.getImageUri()).placeholder(R.drawable.profile).into(binding.imguser);
                    }

                } else {
                    Toast.makeText(requireContext(), "No Users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), RegisterActivity.class);
                intent.putExtra("data", user);
                requireContext().startActivity(intent);
            }
        });


    }
}