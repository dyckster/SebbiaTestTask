package com.example.dyckster.sebbiatesttask.app.ui.news;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.api.Api;
import com.example.dyckster.sebbiatesttask.app.api.Method;
import com.example.dyckster.sebbiatesttask.app.api.Request;
import com.example.dyckster.sebbiatesttask.app.api.Response;
import com.example.dyckster.sebbiatesttask.app.model.news.CompactNews;
import com.example.dyckster.sebbiatesttask.app.ui.BaseFragment;
import com.example.dyckster.sebbiatesttask.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dombaev_yury on 27.12.16.
 */

public class NewsDetailFragment extends BaseFragment {

    private static final String NEWS_ID = "news_id";

    public static NewsDetailFragment newInstance(int newsId) {

        Bundle args = new Bundle();
        args.putInt(NEWS_ID, newsId);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.text_news_title)
    TextView titleText;
    @Bind(R.id.text_news_short_description)
    TextView shortDesc;
    @Bind(R.id.text_news_date)
    TextView dateText;
    @Bind(R.id.text_news_full_description)
    TextView fullDesc;

    int newsId;

    CompactNews news;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_details, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shouldDisplayHomeUp(true);
        if (getArguments() != null) {
            newsId = getArguments().getInt(NEWS_ID);
            getFullNewsDetails();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getFullNewsDetails() {
        news = CompactNews.fromNewsId(newsId);
        if (news != null) {
            fillInfoToScreen();
        } else {
// TODO: 27.12.16 show Progress
        }
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(newsId));
        Api.sendRequestAsync(new Request(Method.GET_DETAILS, params), new Api.OnRequestSentListener() {
            @Override
            public void requestSent(Response response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = response.getJsonObject();
                        if (news != null) {
                            // TODO: 27.12.16 Show progress
                        }
                        asyncUpdate(jsonObject);
                    } else {
                        Log.e("Request error" + response.getErrorMessage());
                    }
                } catch (Exception e) {
                    // TODO: 27.12.16 Stop showing progress
                    Log.e("Error! " + e);
                }
            }
        });

    }

    private void asyncUpdate(final JSONObject jsonObject) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    CompactNews.fromJson(jsonObject);
                } catch (JSONException e) {
                    Log.e("Error!" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                news = CompactNews.fromNewsId(newsId);
                fillInfoToScreen();
                // TODO: 27.12.16 Stop showing progress
            }
        }.execute();
    }

    private void fillInfoToScreen() {
        titleText.setText(news.getTitle());
        setTitle(news.getTitle());
        dateText.setText(news.getDate());
        shortDesc.setText(news.getShortDesc());
        if (news.getFullDescription() != null) {
            fullDesc.setText(Html.fromHtml(news.getFullDescription()));
        }
    }

}
