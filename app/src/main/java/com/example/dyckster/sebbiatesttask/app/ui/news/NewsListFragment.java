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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 26.12.16 set title from category
        setTitle("123456");
        shouldDisplayHomeUp(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected NewsList getUpdatableModel() {
        int categoryId = getActivity().getIntent().getIntExtra(CATEGORY_ID, -1);
        category = Category.fromId(categoryId);

        return new NewsList(category);

    }

    @Override
    protected void updateView(NewsList updatableModel) {
        adapter = new NewsListAdapter(updatableModel);
        recyclerView.setAdapter(adapter);
    }
}
