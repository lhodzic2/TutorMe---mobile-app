package com.example.zavrsnirad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextView registerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerUser = (TextView) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerUser:
                startActivity(new Intent(this,Register.class));
                break;
        }
    }
}