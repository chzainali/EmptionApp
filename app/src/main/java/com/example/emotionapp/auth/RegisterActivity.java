package com.example.emotionapp.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.emotionapp.R;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.auth.model.User;
import com.example.emotionapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    String imageUri = "";
    int PICK_IMAGE_GALLERY = 124;
    FirebaseDatabase database;
    DatabaseReference db;
    FirebaseAuth firebaseAuth;
    User updateModel;
    User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        user = (User) getIntent().getSerializableExtra("data");
        db = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        if (user != null) {
            updateModel = user;
            binding.etFirstName.setText(user.getfName());
            binding.etLastName.setText(user.getlName());
            binding.etEmail.setText(user.getEmail());
            binding.elPassword.setVisibility(View.GONE);
            binding.llBottom.setVisibility(View.GONE);
            binding.etEmail.setEnabled(false);
            Glide.with(this).load(user.getImageUri()).placeholder(R.drawable.profile).into(binding.ivProfile);
            binding.btnRegister.setText("Update Profile");
            imageUri = user.getImageUri();
            binding.tvTitle.setText("Update Profile");
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("It takes Just a few Seconds... ");
                progressDialog.setIcon(R.drawable.happy);
                progressDialog.setCancelable(false);

                String name = binding.etFirstName.getText().toString();
                String lName = binding.etLastName.getText().toString();
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                } else if (lName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter last Name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please enter email in correct format", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty() && user == null) {
                    Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog.show();
                    if (user == null) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String id = FirebaseAuth.getInstance().getUid();
                                try {
                                    signUp(id, name, lName, email, progressDialog);
                                } catch (FileNotFoundException e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String id = FirebaseAuth.getInstance().getUid();
                        if (!Objects.equals(imageUri, user.getImageUri())) {
                            try {
                                signUp(id, name, lName, email, progressDialog);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            User user = new User(id, name, lName, email, imageUri, updateModel.getCoins());
                            db.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        finish();
                                        Toast.makeText(RegisterActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                }
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ivProfile.setOnClickListener(v -> {

            Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uriImage = data.getData();
            try {
                imageUri = uriImage.toString();
                Glide.with(this).load(imageUri).placeholder(R.drawable.profile).into(binding.ivProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void signUp(String userId, String name, String lastName, String email, ProgressDialog progressDialog) throws FileNotFoundException {

        if (!TextUtils.isEmpty(imageUri)) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReference();
            StorageReference profileRef = reference.child("images" + userId + ".jpg");
            InputStream inputStream = getContentResolver().openInputStream(Uri.parse(imageUri));

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = profileRef.putBytes(data);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return profileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        long coins = 0;
                        if (updateModel!= null) {
                            coins = updateModel.getCoins();
                        }
                        User user = new User(userId, name, lastName, email, downloadUri.toString(), coins);

                        db.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    if (updateModel != null) {
                                        Toast.makeText(RegisterActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                    }
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            User user = new User(userId, name, lastName, email, "", 0L);

            db.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

}