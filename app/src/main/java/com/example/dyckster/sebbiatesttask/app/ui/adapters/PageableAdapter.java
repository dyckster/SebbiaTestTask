package com.example.dyckster.sebbiatesttask.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.sebbia.Model;
import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.app.model.PageableModel;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableModel;
import com.example.dyckster.sebbiatesttask.app.model.news.CompactNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public abstract class PageableAdapter<E extends CompactNews>
        extends RecyclerView.Adapter
        implements PageableModel.PageableListener,
        UpdatableModel.UpdateListener<UpdatableModel> {

    protected PageableModel<E> pageableModel;

    protected List<E> elements;

    private class LoadMoreViewHolder<E> extends ListItemsViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Model element) {

        }

    }

    public PageableAdapter(PageableModel<E> orderList) {
        this.pageableModel = orderList;
        this.elements = new ArrayList<E>(this.pageableModel.getItems());
        this.pageableModel.addListener(this);
        this.pageableModel.getPageableListeners().addWeakListener(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (getContext() != null) {
            switch (CompactNews.ViewType.values()[viewType]) {
                case LOAD_MORE:
                    viewHolder = new LoadMoreViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.load_more_cell, parent, false));
                    break;
                default:
                    viewHolder = getViewHolder(parent, CompactNews.ViewType.values()[viewType]);
                    break;
            }
        }
        return viewHolder;
    }

    protected Context getContext() {
        return SebbiaTestTaskApplication.getInstance().getApplicationContext();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (CompactNews.ViewType.values()[holder.getItemViewType()]) {
            case LOAD_MORE:
                break;
            default:
                if (position < elements.size()) {
                    ((ListItemsViewHolder<E>) holder).bindData(elements.get(position));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return elements.size() + (pageableModel.isPagingInProgress() ? 1 : 0);
    }

    @Override
    public void pagingStarted(PageableModel pageable) {
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if (position < elements.size()) {
            return elements.get(position).getNewsId();
        } else {
            return -1;
        }
    }

    @Override
    public void pageLoaded(PageableModel pageable, boolean success, ServerError error) {
        updateItems();
    }

    @Override
    public void updateStarted(UpdatableModel updatableModel) {

    }

    @Override
    public void onUpdated(UpdatableModel updatableModel, boolean success, ServerError error) {
        updateItems();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == elements.size() && pageableModel.isPagingInProgress()) {
            return CompactNews.ViewType.LOAD_MORE.ordinal();
        } else if (position < elements.size()) {
            return 0;
        } else {
            return CompactNews.ViewType.LOAD_MORE.ordinal();
        }
    }

    private void updateItems() {
        elements = pageableModel.getItems();
        notifyDataSetChanged();
    }
    protected abstract ListItemsViewHolder getViewHolder(ViewGroup parent, CompactNews.ViewType orderType);

    public abstract void swapModel(List<UpdatableModel> items);

}
