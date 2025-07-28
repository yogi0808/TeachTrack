package com.example.teachtrack.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.teachtrack.fragments.SessionFragment;
import com.example.teachtrack.fragments.StudentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    int classId;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int classId) {
        super(fragmentActivity);
        this.classId = classId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("class_id",classId);

        switch (position){
            case 0:
                SessionFragment sessionFragment = new SessionFragment();
                sessionFragment.setArguments(bundle);
                return sessionFragment;
            case 1:
                StudentFragment studentFragment = new StudentFragment();
                studentFragment.setArguments(bundle);
                return studentFragment;
            default:
                SessionFragment defaultFragment = new SessionFragment();
                defaultFragment.setArguments(bundle);
                return defaultFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
