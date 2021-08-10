package com.example.zavrsnirad;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat extends AppCompatActivity {

    private FirebaseUser firebaseUser;
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

        textInput = findViewById(R.id.textInput);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String message = textInput.getText().toString();
            if(!message.equals("")) {
                sendMessage(senderID,recieverID,message);
            }
            textInput.setText("");

        });
        //readMessage(senderID,recieverID);
    }

   private void sendMessage(String senderID, String recieverID, String message) {
        //DocumentReference documentReference = firebaseFirestore.collection("instructors").document(recieverID).collection("messages");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("senderID",senderID);
        hashMap.put("recieverID",recieverID);
        hashMap.put("message",message);
        firebaseFirestore.collection("messages").add(hashMap);
        //TODO: dodati error kad poruka nije poslana


    }
    /*private void setSupportActionBar(Toolbar toolbar) {
    }*/

    private void readMessage(String senderID,String receiverID) {
        messages = new ArrayList<>();
        /*firebaseFirestore.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED && documentChange.getDocument().get("senderID").equals(senderID) && documentChange.getDocument().get("receiverID".equals(receiverID))) {
                        messages.add(documentChange.getDocument().toObject(Message.class));
                    }
                }
                adapter = new MessageAdapter(getApplicationContext(),messages);
                recyclerView.setAdapter(adapter);
            }
        });*/
        //firebaseFirestore.collection("messages").whereEqualTo("sende")
    }


}