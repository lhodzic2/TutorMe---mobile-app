package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zavrsnirad.Fragments.InboxFragment;
import com.example.zavrsnirad.Fragments.SearchFragment;
import com.example.zavrsnirad.Fragments.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView navigationView;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        navigationView = findViewById(R.id.bottom_nav);

        navigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new SearchFragment()).commit();
        /*
        //Tab layout
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager,getLifecycle());
        viewPager2.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager2);


        //dodavanje tabova
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Inbox"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
*/


       /* tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });*/


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
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
    };

    //Log out

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Unutrasnja klasa ViewPageAdapter

    /*class ViewPagerAdapter extends FragmentStateAdapter {
        ArrayList<Fragment> fragments;
        ArrayList<String> strings;

        public ViewPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments, ArrayList<String> strings) {
            super(fragmentActivity);
            this.fragments = fragments;
            this.strings = strings;
        }

        public ViewPagerAdapter(@NonNull @NotNull Fragment fragment, ArrayList<Fragment> fragments, ArrayList<String> strings) {
            super(fragment);
            this.fragments = fragments;
            this.strings = strings;
        }

        public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle, ArrayList<Fragment> fragments, ArrayList<String> strings) {
            super(fragmentManager, lifecycle);
            this.fragments = fragments;
            this.strings = strings;
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }


    }*/


}