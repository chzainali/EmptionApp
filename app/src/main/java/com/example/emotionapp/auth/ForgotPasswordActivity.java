package com.example.emotionapp.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emotionapp.R;
import com.example.emotionapp.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("It takes Just a few Seconds... ");
                progressDialog.setIcon(R.drawable.happy);
                progressDialog.setCancelable(false);
                String email = binding.etEmail.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter email in correct format", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your email for reset the password", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}