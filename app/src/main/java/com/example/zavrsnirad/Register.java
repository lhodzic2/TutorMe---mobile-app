package com.example.zavrsnirad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText firstName, lastName,email,password;
    private RadioButton studentRadio, instruktorRadio;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private String userID;

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
        studentRadio = findViewById(R.id.studentRadio);
        instruktorRadio = findViewById(R.id.instruktorRadio);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
    }

    public void handleRegister(View view) {
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (! (validatePassword(passwordText) && validateEmail(emailText) && validateFirstName(firstNameText) && validateLastName(lastNameText) )) return;
        if (!studentRadio.isChecked() && !instruktorRadio.isChecked()) {
            Toast.makeText(Register.this,"Molimo označite da li ste student ili instruktor",Toast.LENGTH_LONG);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(task -> { //vec provjerava da li je korisnik registrovan
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
                userID = firebaseAuth.getCurrentUser().getUid();
                DocumentReference documentReference = firebaseFirestore.collection("students").document(userID);
                if (!studentRadio.isChecked()) {
                    documentReference = firebaseFirestore.collection("instructors").document(userID);
                }
                Map<String,Object> hashMap = new HashMap<>();
                hashMap.put("ime",firstNameText);
                hashMap.put("prezime",lastNameText);
                hashMap.put("email",emailText);
                documentReference.set(hashMap);
                if (instruktorRadio.isChecked()) {
                    startActivity(new Intent(getApplicationContext(), PickingSubjects.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                }
                //finish();
            } else {
                Toast.makeText(Register.this,"Error!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
//TODO: popraviti progressbar
    //TODO:pohvatati izuzetke
    //TODO: refaktorisati kod

    public void handleLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}