package com.example.zavrsnirad;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zavrsnirad.model.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.

 */
public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<User> userList;
    MyAdapter myAdapter;
    FirebaseFirestore firebaseFirestore;

    public SearchFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /*recyclerView = container.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        firebaseFirestore = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        myAdapter = new MyAdapter(this.getContext(),userList);
        recyclerView.setAdapter(myAdapter);*/
        //EventChangeListener();
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;
    }


}