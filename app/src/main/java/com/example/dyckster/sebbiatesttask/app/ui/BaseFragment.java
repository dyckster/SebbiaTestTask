package com.example.dyckster.sebbiatesttask.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public abstract class BaseFragment extends Fragment {
    protected MainActivity getMainActivity() {
        if (getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        } else {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void setNavigationAndTitle(String title, boolean displayUpButton) {
        setTitle(title);
        if (displayUpButton) {
            shouldDisplayHomeUp(displayUpButton);
        }

    }

    protected void setTitle(String title) {
        if (getMainActivity() != null) {
            getMainActivity().getSupportActionBar().setTitle(title);
        }
    }

    protected void showProgress(View background, View progress, boolean show) {
        if (background != null) {
            background.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (progress != null) {
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void shouldDisplayHomeUp(boolean should) {
        if (getMainActivity() != null) {
            ActionBar bar;
            if (getMainActivity().getSupportActionBar() != null) {
                bar = getMainActivity().getSupportActionBar();
            } else {
                return;
            }
            bar.setDisplayHomeAsUpEnabled(should);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

}
