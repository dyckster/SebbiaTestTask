package com.example.dyckster.sebbiatesttask.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.app.model.PageableModel;

/**
 * Created by dombaev_yury on 26.12.16
 */

public abstract class PageableListFragment<T extends PageableModel>
        extends UpdatableListFragment<T>
        implements PageableModel.PageableListener<T> {
    T pageableModel;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    final int lastVisible = layoutManager.findLastVisibleItemPosition();
                    if (recyclerView.getAdapter().getItemCount() > 0) {
                        if (recyclerView.getAdapter().getItemCount() == lastVisible + 1) {
                            if (updatableModel.hasMore() && !updatableModel.isUpdateInProgress()) {
                                updatableModel.loadNextPage();
                            }
                            layoutManager.scrollToPosition(updatableModel.getItems().size());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updatableModel.addPageableListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        updatableModel.removePageableListener(this);
    }

    @Override
    public void pagingStarted(T pageable) {

    }

    @Override
    public void pageLoaded(T pageable, boolean success, ServerError error) {
        if (success) {
            updateView(pageable);
        } else if (!success && isResumed()) {
            Toast.makeText(getActivity(), "Service unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
