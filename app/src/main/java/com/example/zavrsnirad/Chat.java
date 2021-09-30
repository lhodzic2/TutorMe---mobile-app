package com.example.zavrsnirad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zavrsnirad.Adapter.MessageAdapter;
import com.example.zavrsnirad.model.Message;
import com.example.zavrsnirad.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private FirebaseFirestore firebaseFirestore;
    private Intent intent;
    private EditText textInput;
    private ImageButton btnSend,btnClose;
    private MessageAdapter adapter;
    private List<Message> messages;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView userName = findViewById(R.id.userFullName);
        ImageView imageView = findViewById(R.id.profileIconChat);
        btnClose = findViewById(R.id.close);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true); //TODO:provjeriti
        recyclerView.setLayoutManager(layoutManager);

        btnClose.setOnClickListener(v -> {
            finish();
        });


        intent = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String senderID = firebaseUser.getUid();
        String recieverID = intent.getStringExtra("id");

        //dobavljanje imena primatelja
        DocumentReference document = firebaseFirestore.collection("users").document(recieverID);
        document.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            userName.setText(user.getFullName());
            readMessages(senderID.trim(), recieverID.trim());
            if (user.getImageURI().equals("default")) {
                imageView.setImageResource(R.mipmap.ikona3);
            } else {
                Glide.with(getApplicationContext())
                        .load(user.getImageURI())
                        .into(imageView);
            }

        });


        textInput = findViewById(R.id.textInput);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String message = textInput.getText().toString();
            if (!message.equals("")) {
                sendMessage(senderID, recieverID, message);
            }
            textInput.setText("");

        });
    }

    private void sendMessage(String senderID, String recieverID, String message) {
        DatabaseReference db = FirebaseDatabase.getInstance("https://zavrsni-rad-200d4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderID", senderID);
        hashMap.put("receiverID", recieverID.trim());
        hashMap.put("message", message);
        db.child("messages").push().setValue(hashMap);

        DatabaseReference dbChat = FirebaseDatabase.getInstance("https://zavrsni-rad-200d4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("MessageList")
                .child(senderID).child(recieverID);
        dbChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    dbChat.child("id").setValue(recieverID);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DatabaseReference dbChat1 = FirebaseDatabase.getInstance("https://zavrsni-rad-200d4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("MessageList")
                .child(recieverID).child(senderID);
        dbChat1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    dbChat1.child("id").setValue(senderID);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void readMessages(String senderID, String receiverID) {
        messages = new ArrayList<>();
        db = FirebaseDatabase.getInstance("https://zavrsni-rad-200d4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    Message message = ds.getValue(Message.class);

                    if (message.getSenderID().equals(senderID) && message.getReceiverID().equals(receiverID) || message.getReceiverID().equals(senderID) && message.getSenderID().equals(receiverID)) {
                        messages.add(message);
                    }
                    adapter = new MessageAdapter(Chat.this, messages);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}