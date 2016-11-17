package com.example.dyckster.sebbiatesttask.ui;

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
import com.example.dyckster.sebbiatesttask.api.Method;
import com.example.dyckster.sebbiatesttask.api.RestCall;
import com.example.dyckster.sebbiatesttask.model.NewsAdapter;
import com.example.dyckster.sebbiatesttask.model.NewsList;
import com.example.dyckster.sebbiatesttask.tools.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class NewsListFragment extends Fragment {

    private static final String CATEGORY_ID = "category_id";

    private String categoryId;

    private OnFragmentChange mListener;

    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;

    public NewsListFragment() {
        // Required empty public constructor
    }


    public static NewsListFragment newInstance(String param1) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getString(CATEGORY_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }


    private void listNews(long page) {
        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("page", String.valueOf(page));
        RestCall.apiCall(Method.GET_NEWS_LIST, categoryId, bodyParams, new RestCall.OnRestCallListener() {
            @Override
            public void onSuccess(JSONObject object) throws JSONException {
                Gson gson = new Gson();
                NewsList newsList = gson.fromJson(object.toString(), NewsList.class);
                newsAdapter = new NewsAdapter(newsList.getList(), mListener);
                recyclerView.setAdapter(newsAdapter);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onConnectionError() {

            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                listNews(page);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_news);
        setupRecyclerView();
        listNews(0);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChange) {
            mListener = (OnFragmentChange) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
