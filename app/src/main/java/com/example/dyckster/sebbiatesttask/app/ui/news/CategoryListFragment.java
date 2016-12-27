package com.example.dyckster.sebbiatesttask.app.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dyckster.sebbiatesttask.app.model.news.CategoriesList;
import com.example.dyckster.sebbiatesttask.app.ui.UpdatableListFragment;
import com.example.dyckster.sebbiatesttask.app.ui.adapters.CategoriesAdapter;

public class CategoryListFragment extends UpdatableListFragment<CategoriesList> {

    private CategoriesAdapter categoriesAdapter;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("News!");
        shouldDisplayHomeUp(false);

    }

    @Override
    protected CategoriesList getUpdatableModel() {
        return CategoriesList.getInstance();
    }

    @Override
    protected void updateView(CategoriesList updatableModel) {
        categoriesAdapter = new CategoriesAdapter(updatableModel.getItems());
        recyclerView.setAdapter(categoriesAdapter);

    }


}
