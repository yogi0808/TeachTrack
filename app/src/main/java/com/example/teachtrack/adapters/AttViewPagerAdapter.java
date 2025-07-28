package com.example.teachtrack.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.teachtrack.fragments.AbsentStudentFragment;
import com.example.teachtrack.fragments.PresentStudentFragment;

public class AttViewPagerAdapter extends FragmentStateAdapter {
    int sessionId,classId;

    public AttViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,int sessionId,int classId){
        super(fragmentActivity);
        this.sessionId = sessionId;
        this.classId = classId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("session_id",sessionId);
        bundle.putInt("class_id",classId);

        switch (position){
            case 0:
                PresentStudentFragment presentFragment = new PresentStudentFragment();
                presentFragment.setArguments(bundle);
                return presentFragment;
            case 1:
                AbsentStudentFragment absentFragment = new AbsentStudentFragment();
                absentFragment.setArguments(bundle);
                return absentFragment;
            default:
                PresentStudentFragment defaultFragment = new PresentStudentFragment();
                defaultFragment.setArguments(bundle);
                return defaultFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
