package com.example.zavrsnirad.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserProfileFragment extends Fragment {

    private TextView userName;
    private ImageView image;
    private Button btnDelete,btnSave;
    private Uri imageUri;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;

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
        storage = FirebaseStorage.getInstance();
        btnDelete = view.findViewById(R.id.btnDelete);
        btnSave = view.findViewById(R.id.btnSave);

        btnDelete.setOnClickListener(v -> {
            FirebaseAuth.getInstance().getCurrentUser().delete();

        });

        image.setOnClickListener(v -> {
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);//TODO:provjeriti
            mGetContent.launch("image/*");

        });
        
        btnSave.setOnClickListener(v -> {
            uploadImage();
        });



        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
        });

        return view;
    }

    private void uploadImage() {
        if (imageUri != null) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference reference = storage.getReference().child("images/" + userID);
            reference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),"Slika profila uspješno promijenjena!",Toast.LENGTH_LONG).show();
                }
            });
        }
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

}