package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PickingSubjects extends AppCompatActivity {
    ListView listView;
    List list = new ArrayList();
    ArrayAdapter adapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_subjects);
        listView = findViewById(R.id.subjectList);
        list.add("Matematika");
        list.add("Fizika");
        list.add("Hemija");

        adapter = new ArrayAdapter(PickingSubjects.this, android.R.layout.simple_list_item_multiple_choice,list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_done) {
            String selectedItems ="Selected items: \n";
            for (int i = 0; i < listView.getCount(); i++) {
                if (listView.isItemChecked(i)) {
                    selectedItems += listView.getItemAtPosition(i) + "\n";
                }
            }
            Toast.makeText(this,selectedItems,Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}