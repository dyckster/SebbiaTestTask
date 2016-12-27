package com.example.dyckster.sebbiatesttask.app.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.dyckster.sebbiatesttask.app.model.news.Category;
import com.example.dyckster.sebbiatesttask.app.model.news.NewsList;
import com.example.dyckster.sebbiatesttask.app.ui.PageableListFragment;
import com.example.dyckster.sebbiatesttask.app.ui.adapters.NewsListAdapter;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public class NewsListFragment extends PageableListFragment<NewsList> {

    private NewsListAdapter adapter;

    public static final String CATEGORY_ID = "category_id";
    private Category category;

    public static NewsListFragment newInstance(long categoryId) {

        Bundle args = new Bundle();
        args.putLong(CATEGORY_ID, categoryId);
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 26.12.16 set title from category
        setTitle(category.getName());
        shouldDisplayHomeUp(true);

        adapter = new NewsListAdapter(updatableModel);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected NewsList getUpdatableModel() {
        long categoryId = getArguments().getLong(CATEGORY_ID);
        category = Category.fromId((int) categoryId);

        return NewsList.getInstance(category);

    }

    @Override
    protected void updateView(NewsList updatableModel) {
        adapter.swapModel(updatableModel.getItems());
    }
}
