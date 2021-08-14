package com.example.zavrsnirad.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    private UserMessageAdapter adapter;
    private List<User> users;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private List<MessageList> messages;
    private RecyclerView recycler;

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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MessageList messageList = ds.getValue(MessageList.class);
                    messages.add(messageList);
                }
                getAllMessages();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }

    private void getAllMessages() {
        users = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        //TODO:add value event
        firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() { //TODO:popraviti ovaj event listener
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                users.clear();

                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    User user = documentChange.getDocument().toObject(User.class);

                    for (MessageList msg : messages) {
                        if (user.getId().equals(msg.getId())) {
                            users.add(user);
                        }
                    }
                }

                adapter = new UserMessageAdapter(getContext(),users);
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
}