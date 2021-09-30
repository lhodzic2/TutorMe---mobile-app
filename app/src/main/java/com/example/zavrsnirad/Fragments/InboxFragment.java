package com.example.zavrsnirad.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.zavrsnirad.Adapter.UserMessageAdapter;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.MessageList;
import com.example.zavrsnirad.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxFragment extends Fragment {

    private UserMessageAdapter adapter;
    private List<User> users;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private List<MessageList> messages;
    private RecyclerView recycler;
    private ProgressBar progressInbox;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        progressInbox = view.findViewById(R.id.progressInbox);
        recycler = view.findViewById(R.id.recyclerInbox);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messages = new ArrayList<>();

        db = FirebaseDatabase.getInstance("https://zavrsni-rad-200d4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("MessageList").child(userID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messages.clear();
                progressInbox.setVisibility(View.VISIBLE);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MessageList messageList = ds.getValue(MessageList.class);
                    messages.add(messageList);
                }
                getChats();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }

    private void getChats() {
        users = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").addSnapshotListener((value, error) -> {
            users.clear();

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                User user = documentChange.getDocument().toObject(User.class);

                for (MessageList msg : messages) {
                    if (user.getId().equals(msg.getId())) {
                        users.add(user);
                    }
                }
            }
            Collections.reverse(users);
            adapter = new UserMessageAdapter(getContext(),users);
            recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
        progressInbox.setVisibility(View.INVISIBLE);
    }
}