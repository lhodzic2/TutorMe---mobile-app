package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextView registerUser,forgotPassword;
    private EditText email,password;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerUser = findViewById(R.id.registerUser);
        forgotPassword = findViewById(R.id.forgotPassword);
        registerUser.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar2);
        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerUser:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this,Register.class));
                finish();
                break;
            case R.id.btnLogin:
                progressBar.setVisibility(View.VISIBLE);
                userLogin();
                break;
            case R.id.forgotPassword:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(this,ForgotPassword.class));
                break;
        }
    }

    private boolean validateEmail(String emailText) {
        if (emailText.isEmpty()) {
            email.setError("Email je obavezan!");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Unesite validan email!");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }

    private boolean validatePassword(String passwordText) {
        if (passwordText.isEmpty()) {
            password.setError("Password je obavezan!");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        } else if (passwordText.length() < 8) {
            password.setError("Password mora imati najmanje 8 karaktera!");
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }

    private void userLogin() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (!(validateEmail(emailText) && validatePassword(passwordText))) return;

        firebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()) {
                    startActivity(new Intent(this,Dashboard.class));
                    finish();
                } else {
                    user.sendEmailVerification();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Login.this,"Molimo verificirajte svoj email",Toast.LENGTH_LONG).show();
                }
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this,"Prijava neuspješna! Molimo provjerite vaše kredencijale." ,Toast.LENGTH_LONG).show();
            }
        });
    }
}