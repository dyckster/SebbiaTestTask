package com.example.dyckster.sebbiatesttask.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.model.PageableModel;
import com.example.dyckster.sebbiatesttask.app.model.news.CompactNews;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public class NewsListAdapter extends PageableAdapter {
    public NewsListAdapter(PageableModel orderList) {
        super(orderList);
        setHasStableIds(true);
    }

    @Override
    protected ListItemsViewHolder getViewHolder(ViewGroup parent, CompactNews.ViewType orderType) {
        final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new NewsItemViewHolder(inflater.inflate(R.layout.item_listed_news, parent, false));

    }
}
