package com.example.dyckster.sebbiatesttask.app.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.api.Api;
import com.example.dyckster.sebbiatesttask.app.api.Method;
import com.example.dyckster.sebbiatesttask.app.api.Request;
import com.example.dyckster.sebbiatesttask.app.api.Response;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableModel;
import com.example.dyckster.sebbiatesttask.app.model.news.CategoriesList;
import com.example.dyckster.sebbiatesttask.app.ui.OnFragmentChange;
import com.example.dyckster.sebbiatesttask.app.ui.UpdatableListFragment;
import com.example.dyckster.sebbiatesttask.app.ui.adapters.CategoriesAdapter;

public class CategoryListFragment extends UpdatableListFragment<CategoriesList> {

    private CategoriesAdapter categoriesAdapter;
    private OnFragmentChange onFragmentChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    protected CategoriesList getUpdatableModel() {
        return CategoriesList.getInstance();
    }

    @Override
    protected void updateView(CategoriesList updatableModel) {
        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChange) {
            onFragmentChangeListener = (OnFragmentChange) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentChangeListener = null;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_categories);
        setupRecyclerView(recyclerView);
        //callCategoriesList();
    }

//    private void callCategoriesList() {
//        Method method = Method.GET_CATEGORIES;
//        Api.sendRequestAsync(new Request(method), new Api.OnRequestSentListener() {
//            @Override
//            public void requestSent(Response response) {
//                if (response.isSuccessful()) {
//
//                } else {
//                    //do bad stuff
//                }
//            }
//        });
//    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (CategoriesList.getInstance().needsUpdate()) {
            CategoriesList.getInstance().update(true);
            CategoriesList.getInstance().addListener(new UpdatableModel.UpdateListener<UpdatableModel>() {
                @Override
                public void updateStarted(UpdatableModel updatableModel) {

                }

                @Override
                public void onUpdated(UpdatableModel updatableModel, boolean success, ServerError error) {
                    recyclerView.setAdapter(new CategoriesAdapter(CategoriesList.getInstance().getCategories(), onFragmentChangeListener));

                }
            });
        } else {
            recyclerView.setAdapter(new CategoriesAdapter(CategoriesList.getInstance().getCategories(), onFragmentChangeListener));
        }
    }


}
