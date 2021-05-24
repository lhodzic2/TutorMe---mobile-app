package com.example.zavrsnirad;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText firstName, lastName,email,password;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private boolean validatePassword(String passwordText) {
        if (passwordText.isEmpty()) {
            password.setError("Password je obavezan!");
            return false;
        } else if (passwordText.length() < 8) {
            password.setError("Password mora imati najmanje 8 karaktera!");
            return false;
        }
        return true;
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

    private boolean validateFirstName(String firstNameText) {
        if (firstNameText.isEmpty()) {
            firstName.setError("Ime je obavezno!");
            return false;
        }
        return true;
    }

    private boolean validateLastName(String lastNameText) {
        if (lastNameText.isEmpty()) {
            lastName.setError("Prezime je obavezno!");
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.passwordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    public void handleRegister(View view) {
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (! (validatePassword(passwordText) && validateEmail(emailText) && validateFirstName(firstNameText) && validateLastName(lastNameText) )) return;

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Register.this, "Verifikacioni email je poslan na Vašu email adresu!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                startActivity(new Intent(getApplicationContext(), Login.class));
            } else {
                Toast.makeText(Register.this,"Error!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
//TODO: popraviti progressbar

    public void handleLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(getApplicationContext(), Login.class));
    }
}