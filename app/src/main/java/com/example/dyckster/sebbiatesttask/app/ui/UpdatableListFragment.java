package com.example.dyckster.sebbiatesttask.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableList;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public abstract class UpdatableListFragment<T extends UpdatableList> extends UpdatableFragment<T> {
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;

    @Override
    protected View getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.updatable_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.items);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected boolean modelIsEmpty() {
        return updatableModel.getItems().size() == 0;
    }

    @Override
    protected void hideContent() {
        super.hideContent();
        //recyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void showContent() {
        super.showContent();
        recyclerView.setVisibility(View.VISIBLE);
    }

}
