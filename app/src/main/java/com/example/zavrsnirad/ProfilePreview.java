package com.example.zavrsnirad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.Adapter.ReviewAdapter;
import com.example.zavrsnirad.model.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfilePreview extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView userName, descriptionPreview,ratingPreview, subjectsPreview;
    private RecyclerView recyclerView;
    private Button btnSendMessage, btnRate;
    private Intent intent1;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private List<com.example.zavrsnirad.model.Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);
        reviews = new ArrayList<>();

        profilePicture = findViewById(R.id.profilePicturePreview);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnRate = findViewById(R.id.btnRate);
        userName = findViewById(R.id.profileNamePreview);
        descriptionPreview = findViewById(R.id.descriptionPreview);
        ratingPreview = findViewById(R.id.ratingPreview);
        subjectsPreview = findViewById(R.id.subjectsPreview);
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerReview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        intent1 = getIntent();
        userID = intent1.getStringExtra("id");

        btnSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Chat.class);
            intent.putExtra("id", userID);
            startActivity(intent);
            finish();
        });

        btnRate.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Review.class);
            intent.putExtra("id", userID);
            startActivity(intent);
            finish();
        });

        loadReviews();
        DocumentReference document = firebaseFirestore.collection("users").document(userID);
        document.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            descriptionPreview.setText("Opis profila: " + user.getDescription());
            subjectsPreview.setText("Predmeti: " + arrayToString(user.getPredmeti()));
            if (user.getRating() != 0) {
                ratingPreview.setText("Ocjena: " + String.format("%.2f",user.getRating()));
            } else {
                ratingPreview.setText("Nema ocjena :(");
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

    private void loadReviews() {
        firebaseFirestore.collection("users").document(userID).collection("reviews").addSnapshotListener((value, error) -> {
            reviews.clear();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                reviews.add(documentChange.getDocument().toObject(com.example.zavrsnirad.model.Review.class));
            }
            ReviewAdapter reviewAdapter = new ReviewAdapter(getApplicationContext(),reviews);
            recyclerView.setAdapter(reviewAdapter);
            reviewAdapter.notifyDataSetChanged();

        });
    }

    private String arrayToString (ArrayList<String> subjects) {
        String arrayString = subjects.get(0);
        for (int i = 1; i < subjects.size(); i++) {
            arrayString = arrayString + ", " + subjects.get(i);

        }
        return arrayString;
    }
}