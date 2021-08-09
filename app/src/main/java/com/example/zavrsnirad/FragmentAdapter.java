package com.example.zavrsnirad;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.zavrsnirad.Fragments.InboxFragment;
import com.example.zavrsnirad.Fragments.SearchFragment;
import com.example.zavrsnirad.Fragments.UserProfileFragment;

import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new InboxFragment();

            case 2:
                return new UserProfileFragment();
        }
        return new SearchFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
