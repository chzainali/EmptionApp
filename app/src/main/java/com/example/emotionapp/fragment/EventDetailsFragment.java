package com.example.emotionapp.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.emotionapp.R;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.ImageModel;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.adapter.AddImagesAdapter;
import com.example.emotionapp.databinding.FragmentEventDetailsBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.OnItemClick;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EventDetailsFragment extends Fragment {

    FragmentEventDetailsBinding binding;
    EventsDto eventsDto;
    ExoPlayer exoPlayer;
    MainActivity activity;
    List<String> imagesList = new ArrayList<>();

    public EventDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventsDto = (EventsDto) getArguments().getSerializable("data");
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (eventsDto != null) {
            binding.topBar.tvTitle.setText(Utils.formatDate(eventsDto.getTime()));
            binding.rvEmotion.setImageDrawable(Utils.getIcon(activity, eventsDto.getMood()));
            binding.etDetails.setText(eventsDto.getDetails());
            binding.etDetails.setFocusable(false);
            imagesList.clear();
            if (eventsDto.getImages() != null) {
                for (ImageModel images : eventsDto.getImages().values()) {
                    imagesList.add(images.getUrl());
                }
            }
            if (imagesList.isEmpty()) {
                binding.cardView3.setVisibility(View.GONE);
            } else {
                binding.rvImage.setAdapter(new AddImagesAdapter(imagesList, activity, new OnItemClick() {
                    @Override
                    public void onClick(int position) {

                    }
                }));
                binding.cardView3.setVisibility(View.VISIBLE);
            }
            if (!eventsDto.getVoice().isEmpty()) {
                binding.cardView4.setVisibility(View.VISIBLE);
                setPlayer();
            } else {
                binding.cardView4.setVisibility(View.GONE);
            }

            binding.btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (exoPlayer != null) {
                        if (exoPlayer.isPlaying()) {
                            exoPlayer.pause();
                        } else {
                            setPlayer();
                        }
                    } else {
                        setPlayer();
                    }
                }
            });
        }
        binding.topBar.btnDone.setVisibility(View.GONE);
        binding.topBar.btnBack.setOnClickListener(view1 -> findNavController(EventDetailsFragment.this).navigateUp());

    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String title = "Title_" + System.currentTimeMillis();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    private void setPlayer() {
        exoPlayer = new ExoPlayer.Builder(activity).build();
        exoPlayer.setMediaItem(MediaItem.fromUri(eventsDto.getVoice()));
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    binding.btnRecord.setImageResource(R.drawable.pause);
                    binding.tvStatus.setText("Pause");
                } else {
                    binding.btnRecord.setImageResource(R.drawable.play);
                    binding.tvStatus.setText("Play");

                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.pause();
            exoPlayer.release();
        }
    }
}