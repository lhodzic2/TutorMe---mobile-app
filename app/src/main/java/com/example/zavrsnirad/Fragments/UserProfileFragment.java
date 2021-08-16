package com.example.zavrsnirad.Fragments;

import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserProfileFragment extends Fragment {

    private TextView userName;
    private ImageView image;
    private Button btnDelete,btnSave;
    private Uri imageUri;
    private String userID;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private StorageTask uploadTask;


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

        //storageReference = FirebaseStorage.getInstance().getReference();

        btnDelete = view.findViewById(R.id.btnDelete);
        btnSave = view.findViewById(R.id.btnSave);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnDelete.setOnClickListener(v -> {
            FirebaseAuth.getInstance().getCurrentUser().delete();

        });

        image.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
        
        btnSave.setOnClickListener(v -> {
            if (image != null) uploadImage();
        });

        storageReference = FirebaseStorage.getInstance().getReference().child(userID);
        try {
            File localFile = File.createTempFile("tempfile",".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }


        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference reference = firebaseFirestore.collection("users").document(userID);

        reference.addSnapshotListener((value, error) -> {
            if (getContext() == null) return;
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            if (user.getImageURI().equals("default")) {
                image.setImageResource(R.mipmap.ikona3);
            } else {
                Glide.with(getContext()).load(user.getImageURI()).into(image);
            }
        });


        return view;
    }

    /*private void uploadImage() {
        storageReference = FirebaseStorage.getInstance().getReference().child(userID + "." + getFileExtension(imageUri));
        storageReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadTask = storageReference.getFile(imageUri);
                uploadTask.continueWith((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task1 -> {
                    if (!task1.isSuccessful()) {
                        throw task1.getException();
                    }
                    return storageReference.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);
                            document.update("imageURI",mUri);
                            Toast.makeText(getContext(),"Slika profila uspješno promijenjena",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(),"FAIL",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);
                document.update("imageURI",imageUri.toString());
                Toast.makeText(getContext(),"Slika profila uspješno promijenjena",Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }

        });
    }*/
    private void uploadImage() {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(userID + "." + getFileExtension(imageUri));
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DocumentReference document = FirebaseFirestore.getInstance().collection("users").document(userID);
                        document.update("imageURI",uri.toString());
                        Toast.makeText(getContext(),"Slika profila uspješno promijenjena",Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(),"RADI",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(),"FAIL",Toast.LENGTH_LONG).show();
            }
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


}