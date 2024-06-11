package com.example.emotionapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.emotionapp.R;
import com.example.emotionapp.Utils;
import com.example.emotionapp.adapter.AddImagesAdapter;
import com.example.emotionapp.adapter.ModesAdapter;
import com.example.emotionapp.databinding.ActivityAddEventBinding;
import com.example.emotionapp.models.EventsDto;
import com.example.emotionapp.models.OnItemClick;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {
    ActivityAddEventBinding binding;
    List<String> imagesList = new ArrayList<>();
    List<Integer> list = new ArrayList<>();

    AddImagesAdapter adapter;
    ModesAdapter modesAdapter;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int mood = -1;


    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final String LOG_TAG = "AudioRecording";
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String outputFile;

    ProgressDialog progressDialog;
    Long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        time = getIntent().getLongExtra("time", System.currentTimeMillis());
        binding.topBar.tvTitle.setText(Utils.formatDate(time));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("events");
        storageReference = FirebaseStorage.getInstance().getReference("imagesData");
        outputFile = getCacheDir().getAbsolutePath() + "/recording.3gp";
        checkPermissions();
        setImages();
        setMoods();

        binding.topBar.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = binding.etDetails.getText().toString();
                if (details.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Details is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mood == -1) {
                    Toast.makeText(AddEventActivity.this, "Select Mood", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = databaseReference.push().getKey();
                String date = binding.topBar.tvTitle.getText().toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                EventsDto eventsDto = new EventsDto(id, date, userId, details, "", time, mood, new HashMap<>());
                progressDialog.show();
                try {
                    uploadImagesAndSaveUrls(imagesList, eventsDto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.btnRecord.setOnClickListener(view -> {
            if (checkPermissions()) {
                if (isRecording) {
                    stopRecording();
                    isRecording = false;
                    binding.tvStatus.setText("Record");

                } else {
                    if (checkPermissions()) {
                        startRecording();
                        isRecording = true;
                        binding.tvStatus.setText("Stop");
                    }
                }
            }
        });
        binding.topBar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setImages() {
        imagesList.clear();
        imagesList.add(null);
        adapter = new AddImagesAdapter(imagesList, this, new OnItemClick() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    ImagePicker.with(AddEventActivity.this)
                            .crop()                    //Crop image(Optional), Check Customization for more option
                            .compress(1024)            //Final image size will be less than 1 MB(Optional)
                            .start();

                } else {
                    imagesList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        binding.rvImage.setAdapter(adapter);
    }

    private void setMoods() {
        list.clear();
        list.add(R.drawable.happy);
        list.add(R.drawable.smile);
        list.add(R.drawable.neutral);
        list.add(R.drawable.sad);
        list.add(R.drawable.angry);

        modesAdapter = new ModesAdapter(list, this, new OnItemClick() {
            @Override
            public void onClick(int position) {
                AddEventActivity.this.mood = position;
            }
        });
        binding.rvEmotion.setAdapter(modesAdapter);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            showRecordingStatus(true);
            Log.d(LOG_TAG, "Recording started");
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        showRecordingStatus(false);
        Log.d(LOG_TAG, "Recording stopped");
        //uploadAudio();
    }

    private void showRecordingStatus(boolean isRecording) {
        if (isRecording) {
            binding.btnRecord.setImageResource(R.drawable.stop);
            binding.tvStatus.setText("Recording...");
        } else {
            binding.btnRecord.setImageResource(R.drawable.play);
            binding.tvStatus.setText("Recording stopped");
        }
    }

    private void uploadImagesAndSaveUrls(List<String> imageUris, EventsDto eventsDto) throws IOException {
        final int[] uploadedCount = {0};
        if (imagesList.size() > 1) {
            for (String uri : imageUris) {
                // Create a unique filename for each image
                if (uri != null) {
                    String imageName = "image_" + System.currentTimeMillis() + ".jpg";
                    StorageReference imageRef = storageReference.child(imageName);
                    UploadTask uploadTask = imageRef.putStream(getInputStreamFromUri(this, Uri.parse(uri)));

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(it -> {
                            String id = databaseReference.push().getKey();
                            ImageModel imageModel = new ImageModel(id, it.toString());
                            eventsDto.getImages().put(id, imageModel);
                            // Save the URL to the Event model
                            // Check if all images are uploaded
                            uploadedCount[0]++;
                            if (uploadedCount[0] == imagesList.size()) {
                                // All images uploaded, now update the event node
                                uploadAudio(eventsDto);
                            }
                        });
                    }).addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        // Handle failures
                        Toast.makeText(AddEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    uploadedCount[0]++;
                }
            }
        } else {
            uploadAudio(eventsDto);
        }
    }

    private void uploadAudio(EventsDto eventsDto) {
        File file = new File(outputFile);
        if (file.exists()) {
            Uri uri = Uri.fromFile(new File(outputFile));
            StorageReference audioRef = storageReference.child("audio");

            audioRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(AddEventActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        audioRef.getDownloadUrl().addOnSuccessListener(url -> {
                            String downloadUrl = url.toString();
                            // Use the downloadUrl as needed (e.g., save it in the database)
                            Log.d("UPLOAD", "File URL: " + downloadUrl);
                            file.delete();
                            eventsDto.setVoice(downloadUrl);
                            updateEventWithImageUrls(eventsDto);

                        }).addOnFailureListener(exception -> {
                            // Handle any errors retrieving the URL
                            Log.e("UPLOAD", "Failed to retrieve URL: " + exception.getMessage());
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddEventActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            updateEventWithImageUrls(eventsDto);
        }
    }

    private void updateEventWithImageUrls(EventsDto event) {
        // Push the updated event object to Firebase Realtime Database

        databaseReference.child(event.getId()).setValue(event).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            showDialog();
            // Event updated successfully
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            // Handle errors
            Toast.makeText(AddEventActivity.this, "Failed to update event.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String uri = data.getData().toString();
            imagesList.add(uri);
            adapter.notifyDataSetChanged();
        }
    }

    public static InputStream getInputStreamFromUri(Context context, Uri uri) throws IOException, FileNotFoundException {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.openInputStream(uri);
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_coins, null, false);
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(view);
        MaterialButton button = view.findViewById(R.id.btnClaim);
        AlertDialog dialog = dialogBuilder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                progressDialog.show();
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                firebaseDatabase.getReference("Users").child(id).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int coins = 0;
                        if (snapshot.exists()) {
                            coins = snapshot.getValue(Integer.class);
                        }
                        snapshot.getRef().setValue(coins + 100).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    finish();
                                } else {
                                    Toast.makeText(AddEventActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
