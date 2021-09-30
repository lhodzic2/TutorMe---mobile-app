package com.example.zavrsnirad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zavrsnirad.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity {
    private Button btnReview;
    private EditText reviewText;
    private RatingBar ratingBar;
    private float rating;
    private DocumentReference reviewDocument;
    private DocumentReference userDocument;
    private Task<DocumentSnapshot> myDocument;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        btnReview = findViewById(R.id.btnReview);
        reviewText = findViewById(R.id.reviewText);
        ratingBar = findViewById(R.id.ratingBar);

        intent = getIntent();
        String reviewerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userID = intent.getStringExtra("id");
        reviewDocument = FirebaseFirestore.getInstance().collection("users").document(userID).collection("reviews").document(reviewerID);
        userDocument = FirebaseFirestore.getInstance().collection("users").document(userID);


        ratingBar.setOnRatingBarChangeListener((ratingBar, rate, fromUser) -> {
            if (fromUser) rating = rate;

        });

        btnReview.setOnClickListener(v -> {
            if (rating <= 1) {
                Toast.makeText(getApplicationContext(),"Molimo ocijenite korisnika.",Toast.LENGTH_LONG).show();
                return;
            }
            reviewDocument.get().addOnSuccessListener(value -> {
                if (value.exists()) {
                    Toast.makeText(getApplicationContext(),"Ne možete ostaviti komentar dva puta",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    myDocument = FirebaseFirestore.getInstance().collection("users").document(reviewerID).get().addOnSuccessListener(documentSnapshot -> {
                        String reviewerFullName = documentSnapshot.get("fullName").toString();
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("reviewerID", reviewerID);
                        hashMap.put("review", reviewText.getText().toString());
                        hashMap.put("rating",rating);
                        hashMap.put("reviewerName",reviewerFullName);
                        reviewDocument.set(hashMap);

                        userDocument.get().addOnCompleteListener(task -> {
                            User user = task.getResult().toObject(User.class);

                            FirebaseFirestore.getInstance().collection("users/" + userID + "/reviews").get()
                                    .addOnCompleteListener(task1 -> {
                                        int collectionSize = task1.getResult().size();
                                        if (collectionSize != 1) user.setRating((user.getRating() * (collectionSize - 1) + rating) / (float) collectionSize);
                                        else user.setRating((user.getRating() + rating) / (float) collectionSize);
                                        reviewDocument.set(hashMap);
                                        userDocument.update("rating", user.getRating());
                                        Toast.makeText(getApplicationContext(),"Recenzija uspješno poslana!",Toast.LENGTH_LONG).show();
                                        finish();
                                    });
                        });
                    });

                }
            });
        });
    }
}