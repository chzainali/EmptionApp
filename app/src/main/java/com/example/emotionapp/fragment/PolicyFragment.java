package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.FragmentPolicyBinding;


public class PolicyFragment extends Fragment {

    FragmentPolicyBinding binding;
    boolean isPrivacy = true;

    public PolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isPrivacy = getArguments().getBoolean("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPolicyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isPrivacy) {
            binding.textView2.setText(getString(R.string.terms_and_conditions));
            binding.privacyPolicyTextView.setText(getString(R.string.terms_and_conditions_text));
        }
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(PolicyFragment.this).navigateUp();
            }
        });
    }
}