package com.example.zavrsnirad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickingSubjects extends AppCompatActivity {
    ListView listView;
    List<String> list;
    ArrayAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private String userID;
    private List selectedItems = new ArrayList<String>();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_subjects);
        listView = findViewById(R.id.review);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>(Arrays.asList("Matematika", "Fizika", "Informatika", "Bosanski jezik i književnost", "Engleski jezik", "Njemački jezik", "Hemija","Programiranje","Nacrtna geometrija"));
        adapter = new ArrayAdapter(PickingSubjects.this, android.R.layout.simple_list_item_multiple_choice,list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public void handleNext(View view) {
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                selectedItems.add(listView.getItemAtPosition(i).toString());
            }
        }
        userID = firebaseAuth.getCurrentUser().getUid();

        documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("predmeti",selectedItems);
        documentReference.update(hashMap);
        AlertDialog alertDialog = new AlertDialog.Builder(PickingSubjects.this).setTitle("Verifikacija")
                .setMessage("Molimo verificirajte vaš email.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }).create();
        alertDialog.show();
    }
}