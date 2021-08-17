package com.example.zavrsnirad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePreview extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView email,userName,ratingPreview;
    private Button btnSendMessage,btnRate;
    private Intent intent1;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);

        profilePicture = findViewById(R.id.profilePicturePreview);
        email = findViewById(R.id.email);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnRate = findViewById(R.id.btnRate);
        userName = findViewById(R.id.profileNamePreview);
        ratingPreview = findViewById(R.id.ratingPreview);
        firebaseFirestore = FirebaseFirestore.getInstance();

        intent1 = getIntent();
        String userID = intent1.getStringExtra("id");

        btnSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Chat.class);
            intent.putExtra("id",userID);
            startActivity(intent);
            finish();
        });

        btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Review.class);
            intent.putExtra("id",userID);
            startActivity(intent);
            finish();
        });

        DocumentReference document = firebaseFirestore.collection("users").document(userID);
        document.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            email.setText(user.getEmail());
            if (user.getRating() != 0) {
                ratingPreview.setText(Float.toString(user.getRating()));
            }
            if (user.getImageURI().equals("default")) {
                profilePicture.setImageResource(R.mipmap.ikona3);
            } else {
                Glide.with(getApplicationContext())
                        .load(user.getImageURI())
                        .into(profilePicture);
            }

        });
    }
}