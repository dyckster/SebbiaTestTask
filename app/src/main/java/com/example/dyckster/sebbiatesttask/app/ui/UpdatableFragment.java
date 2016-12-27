package com.example.dyckster.sebbiatesttask.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableModel;
import com.example.dyckster.sebbiatesttask.utils.Log;
import com.example.dyckster.sebbiatesttask.utils.Utils;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public abstract class UpdatableFragment<T extends UpdatableModel> extends BaseFragment
        implements UpdatableModel.UpdateListener<T> {
    protected T updatableModel;

    protected ViewGroup rootView;
    protected ViewGroup mainContainer;

    protected SwipeRefreshLayout swipeRefreshLayout;

    protected View emptyData;
    protected View noDataOfflineModeIndicator;
    protected View errorView;

    private IntentFilter connectivityChangeFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityChangeFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.updatableModel = getUpdatableModel();
        if (updatableModel == null) {
            getActivity().finish();
            Log.e(UpdatableModel.class + " cannot be null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(getLayout(), container, false);

        mainContainer = (FrameLayout) rootView.findViewById(R.id.main_container);

        emptyData = inflater.inflate(getEmptyDataLayout(), mainContainer, false);
        emptyData.setVisibility(View.GONE);
        mainContainer.addView(emptyData);

        noDataOfflineModeIndicator = inflater.inflate(R.layout.no_data_offline_mode_indicator, mainContainer, false);
        noDataOfflineModeIndicator.setVisibility(View.GONE);
        mainContainer.addView(noDataOfflineModeIndicator);

        errorView = inflater.inflate(getErrorLayout(), mainContainer, false);
        errorView.setVisibility(View.GONE);
        mainContainer.addView(errorView);

        swipeRefreshLayout = (SwipeRefreshLayout) mainContainer.findViewById(R.id.swipe_container);
        swipeRefreshLayout.addView(getContentView(inflater, swipeRefreshLayout, savedInstanceState));
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updatableModel.update(false);
            }
        });
        return rootView;

    }

    protected int getLayout() {
        return R.layout.updatable;
    }

    protected int getEmptyDataLayout() {
        return R.layout.empty_data;
    }

    protected int getErrorLayout() {
        return R.layout.error;
    }

    protected abstract View getContentView(LayoutInflater inflater,
                                           @Nullable ViewGroup mainContainer,
                                           @Nullable Bundle savedInstanceState);

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(connectionReciever, connectivityChangeFilter);

    }

    @Override
    public void onResume() {
        super.onResume();
        updatableModel.addListener(this);
        updateModel();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        updatableModel.removeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReciever);
    }

    protected abstract boolean modelIsEmpty();

    @Override
    public void updateStarted(T updatableModel) {
        refresh();
    }

    @Override
    public void onUpdated(T updatableModel, boolean success, ServerError error) {
        refresh();
        if (!success && isResumed()) {
            Toast.makeText(getActivity(), "Service unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    protected void refreshSwipe() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(updatableModel.isUpdateInProgress());
            }
        });
    }

    protected void refresh() {
        if (updatableModel != null) {
            refreshSwipe();
            if (updatableModel.hasData()) {
                if (modelIsEmpty()) {
                    Log.d("Has data and its empty");

                    noDataOfflineModeIndicator.setVisibility(View.GONE);
                    emptyData.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    hideContent();
                } else {
                    Log.d("Has data and its NOT empty");

                    noDataOfflineModeIndicator.setVisibility(View.GONE);
                    emptyData.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);

                    showContent();
                    updateView(updatableModel);
                }
            } else {
                hideContent();
                if (updatableModel.isUpdateInProgress()) {
                    Log.d("No data, but update is in progress");

                    noDataOfflineModeIndicator.setVisibility(View.GONE);
                    emptyData.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                } else if (!Utils.isInternetAvailable(getActivity())) {
                    Log.d("No data, no internet");

                    noDataOfflineModeIndicator.setVisibility(View.VISIBLE);
                    emptyData.setVisibility(View.GONE);
                    errorView.setVisibility(View.GONE);
                } else {
                    Log.d("No data, error during last reload");

                    noDataOfflineModeIndicator.setVisibility(View.GONE);
                    emptyData.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    private BroadcastReceiver connectionReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateModel();
        }
    };

    protected void updateModel() {
        if (updatableModel != null && updatableModel.needsUpdate() && !updatableModel.isUpdateInProgress()) {
            updatableModel.update(false);
        }
    }

    protected abstract T getUpdatableModel();

    protected abstract void updateView(T updatableModel);

    protected void hideContent() {
    }

    protected void showContent() {
    }
}
