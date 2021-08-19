package com.example.zavrsnirad.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.Adapter.ReviewAdapter;
import com.example.zavrsnirad.ForgotPassword;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.Review;
import com.example.zavrsnirad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private TextView userName, email,changePassword,profileRating;
    private EditText profileDescription;
    private ImageView image;
    private Button btnDelete,btnSave;
    private Uri imageUri;
    private String userID;
    private List<Review> reviews;
    private RecyclerView recyclerView;

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
        //changePassword = view.findViewById(R.id.changePassword);
        //email = view.findViewById(R.id.email);
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



        btnDelete = view.findViewById(R.id.btnDelete);
        btnSave = view.findViewById(R.id.btnSave);


        /*changePassword.setOnClickListener(v -> {

            startActivity(new Intent(getContext(), ForgotPassword.class));
        });*/

        btnDelete.setOnClickListener(v -> {
            FirebaseAuth.getInstance().getCurrentUser().delete();

        });

        image.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
        
        btnSave.setOnClickListener(v -> {
            String descChange = profileDescription.getText().toString();
            document.update("description",descChange);
            if (imageUri != null) saveChanges();
        });



        reference.addSnapshotListener((value, error) -> {
            if (getContext() == null) return;
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            //email.setText(user.getEmail());

            if (user.getImageURI().equals("default")) {
                image.setImageResource(R.mipmap.ikona3);
            } else {
                Glide.with(getContext()).load(user.getImageURI()).into(image);
            }
            if (user.getType().equals("instructor")) {
                if (user.getRating() != 0) profileRating.setText(Float.toString(user.getRating()));
                else profileRating.setText("Nema ocjena :(");
                profileDescription.setText(user.getDescription());
                loadReviews();
            } else {
                profileDescription.setVisibility(View.INVISIBLE);
            }

        });


        return view;
    }


    private void saveChanges() {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(userID + "." + getFileExtension(imageUri));
        reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            document.update("imageURI",uri.toString());
            Toast.makeText(getContext(),"Promjene uspješno spašene",Toast.LENGTH_LONG).show();
        })).addOnProgressListener(snapshot -> {

        }).addOnFailureListener(e -> Toast.makeText(getContext(),"FAIL",Toast.LENGTH_LONG).show());
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