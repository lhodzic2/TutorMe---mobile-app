package com.example.zavrsnirad.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.Adapter.ReviewAdapter;
import com.example.zavrsnirad.ForgotPassword;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.Review;
import com.example.zavrsnirad.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private TextView userName, email,changePassword,profileRating;
    private EditText profileDescription;
    private ImageView image;
    private Button btnChangePassword,btnSave;
    private Uri imageUri;
    private String userID;
    private List<Review> reviews;
    private RecyclerView recyclerView;
    private ProgressBar progressProfile;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference document;



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
        progressProfile = view.findViewById(R.id.progressProfile);
        profileRating = view.findViewById(R.id.profileRating);
        profileDescription = view.findViewById(R.id.profileDescription);
        reviews = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerProfile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        document = FirebaseFirestore.getInstance().collection("users").document(userID);
        DocumentReference reference = firebaseFirestore.collection("users").document(userID);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSave = view.findViewById(R.id.btnSave);

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ForgotPassword.class));
        });

        image.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
        
        btnSave.setOnClickListener(v -> {
            String descChange = profileDescription.getText().toString();
            document.update("description",descChange);
            if (imageUri != null) saveChanges();
            else if (getContext() != null) Toast.makeText(getContext(),"Uspješno promijenjen opis profila",Toast.LENGTH_SHORT).show();
        });

        reference.addSnapshotListener((value, error) -> {
            if (getContext() == null) return;
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            //email.setText(user.getEmail());

            if (user.getImageURI().equals("default")) {
                image.setImageResource(R.mipmap.ikona3);
            } else {
                progressProfile.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(user.getImageURI()).into(image);
                progressProfile.setVisibility(View.INVISIBLE);

            }
            if (user.getType().equals("instructor")) {
                if (user.getRating() != 0) profileRating.setText(String.format("%.2f",user.getRating()));
                else profileRating.setText("Nema ocjena :(");
                profileDescription.setText(user.getDescription());
                loadReviews();
            } else {
                profileDescription.setVisibility(View.INVISIBLE);
                profileRating.setVisibility(View.INVISIBLE);
            }

        });
        return view;
    }


    private void saveChanges() {
        progressProfile.setVisibility(View.VISIBLE);
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(userID + "." + getFileExtension(imageUri));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            document.update("imageURI",uri.toString());
            profileRating.setVisibility(View.INVISIBLE);
            if (getContext() != null )Toast.makeText(getContext(),"Promjene uspješno spašene",Toast.LENGTH_LONG).show();
        })).addOnProgressListener(snapshot -> {
            progressProfile.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            profileRating.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Greška", Toast.LENGTH_LONG).show();
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        image.setImageURI(uri);
                        imageUri = uri;
                    }
                }
            });

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void loadReviews() {
        firebaseFirestore.collection("users").document(userID).collection("reviews").addSnapshotListener((value, error) -> {
            reviews.clear();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                reviews.add(documentChange.getDocument().toObject(com.example.zavrsnirad.model.Review.class));
            }
            ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(),reviews);
            recyclerView.setAdapter(reviewAdapter);
            reviewAdapter.notifyDataSetChanged();
        });
    }


}