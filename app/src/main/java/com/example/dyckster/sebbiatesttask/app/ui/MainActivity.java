package com.example.dyckster.sebbiatesttask.app.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.ui.news.CategoryListFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentChange {
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.fragment_parent, new CategoryListFragment()).commit();
        }
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_parent, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
    }
}
