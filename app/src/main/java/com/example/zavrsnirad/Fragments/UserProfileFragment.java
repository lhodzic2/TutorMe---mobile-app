package com.example.zavrsnirad.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserProfileFragment extends Fragment {

    private TextView userName;
    private ImageView image;

    private FirebaseFirestore firebaseFirestore;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userName = view.findViewById(R.id.profileName);
        image = view.findViewById(R.id.profilePicture);

        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                userName.setText(user.getFullName());
            }
        });

        return view;
    }
}