package com.example.zavrsnirad;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button resetPasswordBtn;
    private EditText email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressBar = findViewById(R.id.progressBar3);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        email = findViewById(R.id.resetEmail);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean validateEmail(String emailText) {
        if (emailText.isEmpty()) {
            email.setError("Email je obavezan!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Unesite validan email!");
            return false;
        }
        return true;
    }

    public void handleResetPassword(View view) {
        String emailText = email.getText().toString().trim();
        if(! validateEmail(emailText)) return;
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPassword.this, "Mail za obnovu lozinke uspješno poslan!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                email.setText("");
            } else {
                Toast.makeText(ForgotPassword.this, "Greška",Toast.LENGTH_LONG).show();
            }
        });
    }
}