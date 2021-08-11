package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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
    private ImageButton btnSend;
    private MessageAdapter adapter;
    private List<Message> messages;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView userName = findViewById(R.id.userFullName);
        ImageView imageView = findViewById(R.id.profileIconChat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true); //TODO:provjeriti
        recyclerView.setLayoutManager(layoutManager);

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        intent = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String senderID = firebaseUser.getUid();
        String recieverID = intent.getStringExtra("id");

        //dobavljanje imena primatelja
        DocumentReference document = firebaseFirestore.collection("instructors").document(recieverID);
        document.addSnapshotListener((value, error) -> {
            String user = value.getString("fullName");
            userName.setText(user);
            readMessage(senderID.trim(), recieverID.trim());
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
        //TODO: dodati error kad poruka nije poslana
        //TODO: provjeriti da li je listener najbolja opcija
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

    }

    private void readMessage(String senderID, String receiverID) {
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