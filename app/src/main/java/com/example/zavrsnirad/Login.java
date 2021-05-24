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
    private TextView registerUser;
    private EditText email,password;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerUser = (TextView) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        email = (EditText) findViewById(R.id.emailLogin);
        password = (EditText) findViewById(R.id.passwordLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
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
                break;
            case R.id.btnLogin:
                userLogin();
                break;
        }
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

    private void userLogin() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (!(validateEmail(emailText) && validatePassword(passwordText))) return;

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()) {
                    startActivity(new Intent(this,Dashboard.class));
                } else {
                    user.sendEmailVerification();
                    Toast.makeText(Login.this,"Molimo verificirajte svoj email",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Login.this,"Prijava neuspješna! Molimo provjerite vaše kredencijale.",Toast.LENGTH_LONG).show();
            }
        });
    }
}