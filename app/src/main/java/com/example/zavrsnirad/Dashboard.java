package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

//TODO: popraviti restart

public class Dashboard extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        navigationView = findViewById(R.id.bottom_nav);

        navigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new SearchFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.inbox:
                    selectedFragment = new InboxFragment();
                    break;
                case R.id.profile:
                    selectedFragment = new UserProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();
            return true;
        }
    };
}