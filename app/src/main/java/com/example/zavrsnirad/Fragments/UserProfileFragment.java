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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserProfileFragment extends Fragment {

    private TextView userName;
    private ImageView image;
    private Button btnDelete,btnSave;
    private Uri imageUri;
    private Uri uri;
    String mUri;
    private StorageTask uploadTask;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
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
            if (user.getImageURI().equals("default")) {
                image.setImageResource(R.mipmap.ikona3);
            } else {
                Glide.with(getContext()).load(user.getImageURI()).into(image);
            }
        });



        return view;
    }

    private void uploadImage() {
        if (imageUri != null) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference reference = storage.getReference().child(userID);
            uploadTask = reference.getFile(imageUri);
            uploadTask.continueWith(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();

                        DocumentReference document = firebaseFirestore.collection("users").document(userID);
                        Map<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageURI",mUri);
                        document.update(hashMap);
                        Toast.makeText(getContext(),"Slika profila uspješno promijenjena!",Toast.LENGTH_LONG).show();
                    }

                }
            });

            /*reference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    DocumentReference document = firestore.collection("users").document(userID + "." + getFileExtension(imageUri));
                    Map<String,Object> hashMap = new HashMap<>();
                    hashMap.put("imageURI","userProfilePhoto");
                    document.update(hashMap);
                    Toast.makeText(getContext(),"Slika profila uspješno promijenjena!",Toast.LENGTH_LONG).show();
                }
            });*/
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}