package com.example.zavrsnirad.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.zavrsnirad.Adapter.UserAdapter;
import com.example.zavrsnirad.R;
import com.example.zavrsnirad.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> users;
    private SearchView searchView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progressSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        progressSearch = view.findViewById(R.id.progressSearch);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.searchView);
        swipe = view.findViewById(R.id.swipe);

        users = new ArrayList<>();
        loadUsers();

        swipe.setOnRefreshListener(() -> {
            loadUsers();
            swipe.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        progressSearch.setVisibility(View.VISIBLE);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").addSnapshotListener((value, error) -> {
            users.clear();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED && !documentChange.getDocument().getId().equals(userID) && documentChange.getDocument().get("type").equals("instructor")) {
                    users.add(documentChange.getDocument().toObject(User.class));
                }
            }
            userAdapter = new UserAdapter(getContext(),users);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    userAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            recyclerView.setAdapter(userAdapter);
            userAdapter.notifyDataSetChanged();
            progressSearch.setVisibility(View.INVISIBLE);
        });

    }

}