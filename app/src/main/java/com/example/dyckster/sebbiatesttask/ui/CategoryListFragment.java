package com.example.dyckster.sebbiatesttask.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.api.Method;
import com.example.dyckster.sebbiatesttask.api.RestCall;
import com.example.dyckster.sebbiatesttask.model.CategoriesAdapter;
import com.example.dyckster.sebbiatesttask.model.CategoriesList;
import com.example.dyckster.sebbiatesttask.model.Category;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment {

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

        RestCall.apiCall(Method.GET_CATEGORIES, null, null, new RestCall.OnRestCallListener() {
            @Override
            public void onSuccess(JSONObject object) throws JSONException {
                Gson gson = new Gson();
                CategoriesList categoriesList = gson.fromJson(object.toString(), CategoriesList.class);
                categoriesAdapter = new CategoriesAdapter(categoriesList.getList(), onFragmentChangeListener);
                setupRecyclerView(recyclerView);
            }

            @Override
            public void onError() {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectionError() {
                Toast.makeText(getActivity(), "Connection error!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoriesAdapter);
    }


}
