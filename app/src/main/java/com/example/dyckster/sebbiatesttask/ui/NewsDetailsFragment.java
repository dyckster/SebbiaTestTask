package com.example.dyckster.sebbiatesttask.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.api.Method;
import com.example.dyckster.sebbiatesttask.api.RestCall;
import com.example.dyckster.sebbiatesttask.model.News;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NewsDetailsFragment extends Fragment {

    private static final String NEWS_ID = "news_id";
    private static final String NEWS_SHORT_DESCRIPTION = "news_short_desc";
    private static final String NEWS_DATE = "news_date";
    private static final String NEWS_TITLE = "news_title";

    private String newsId;
    private String newsTitle;
    private String newsShortDescription;
    private String newsDate;

    private TextView fullDescriptionTextView;

    public static NewsDetailsFragment newInstance(String newsId, String newsTitle, String shortDesc, String date) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle args = new Bundle();
        args.putString(NEWS_ID, newsId);
        args.putString(NEWS_TITLE, newsTitle);
        args.putString(NEWS_SHORT_DESCRIPTION, shortDesc);
        args.putString(NEWS_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsId = getArguments().getString(NEWS_ID);
            newsTitle = getArguments().getString(NEWS_TITLE);
            newsShortDescription = getArguments().getString(NEWS_SHORT_DESCRIPTION);
            newsDate = getArguments().getString(NEWS_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView shortDescriptionTextView = (TextView) view.findViewById(R.id.text_news_short_description);
        fullDescriptionTextView = (TextView) view.findViewById(R.id.text_news_full_description);
        TextView dateTextView = (TextView) view.findViewById(R.id.text_news_date);
        TextView newsTitleTextView = (TextView) view.findViewById(R.id.text_news_title);

        newsTitleTextView.setText(newsTitle);
        shortDescriptionTextView.setText(newsShortDescription);
        dateTextView.setText(newsDate);
        callNews();
    }

    private void callNews() {
        Map<String, String> body = new HashMap<>();
        body.put("id", newsId);

        RestCall.apiCall(Method.GET_DETAILS, null, body, new RestCall.OnRestCallListener() {
            @Override
            public void onSuccess(JSONObject object) throws JSONException {
                Gson gson = new Gson();

                News news = gson.fromJson(object.getString("news"), News.class);
                fullDescriptionTextView.setText(Html.fromHtml(news.getFullDescription()));
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

}
