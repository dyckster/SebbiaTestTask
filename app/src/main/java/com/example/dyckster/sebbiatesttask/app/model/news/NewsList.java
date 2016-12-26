package com.example.dyckster.sebbiatesttask.app.model.news;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.activeandroid.sebbia.ActiveAndroid;
import com.activeandroid.sebbia.model.OneToManyRelation;
import com.example.dyckster.sebbiatesttask.app.api.Api;
import com.example.dyckster.sebbiatesttask.app.api.Method;
import com.example.dyckster.sebbiatesttask.app.api.Request;
import com.example.dyckster.sebbiatesttask.app.api.Response;
import com.example.dyckster.sebbiatesttask.app.api.ServerError;
import com.example.dyckster.sebbiatesttask.app.model.PageableModel;
import com.example.dyckster.sebbiatesttask.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NewsList extends PageableModel<CompactNews> {

    public static final int PAGE_SIZE = 10;
    private List<CompactNews> news;

    public void setCategory(Category category) {
        this.category = category;
    }

    private Category category;

    public NewsList(Category category){
        this.category = category;
    }

    public List<CompactNews> getList() {
        return news;
    }


    @Override
    public boolean hasMore() {
        return news.size() >= currentPage * PAGE_SIZE;
    }

    @Override
    protected ServerError performPaging(@NonNull AsyncTask<Void, Void, ServerError> runnable, int nextPage) {
        Response response;
        try {
            response = Api.sendRequest(getRequest(nextPage));
        } catch (JSONException ex) {
            Log.e(Log.DEFAULT_TAG, "Exception!" + ex);
            return ServerError.REQUEST_ERROR;
        }
        if (!response.isSuccessful()) {
            return response.getErrorCode();
        }
        try {
            parseAndSave(response);
        } catch (JSONException ex) {
            Log.e(Log.DEFAULT_TAG, "Exception!" + ex);
            return ServerError.UNKNOWN_ERROR;
        }
        return response.getErrorCode();
    }

    @Override
    public List<CompactNews> getItems() {
        return news;
    }

    @Override
    protected long getUpdatePeriod() {
        return TimeUnit.MINUTES.toMillis(5);
    }


    private Request getRequest(int nextPage) throws JSONException {
        Map<String, String> param = new HashMap<>();
        param.put("id", String.valueOf(category.getCategoryId()));
        param.put("page", String.valueOf(nextPage));
        return new Request(Method.GET_NEWS_LIST, param);
    }

    @Override
    protected Request getUpdateRequest() {
        try {
            return getRequest(FIRST_PAGE);
        } catch (JSONException e) {
            Log.e("Error!");
        }
        return null;
    }

    @Override
    protected void parseAndSave(Response response) throws JSONException {
        try {
            ActiveAndroid.beginTransaction();
            JSONArray jsonArray = response.getJsonArray();
            ArrayList<CompactNews> news = new ArrayList<>();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    news.add(CompactNews.fromJson(jsonArray.getJSONObject(i)));
                }
            } else {
            }
            if (currentPage == FIRST_PAGE) {
                this.news = news;
            } else {
                this.news.addAll(news);
            }
            this.save();
            OrderConnections.setRelations(OrderConnections.class, NewsList.this, this.news);
        } catch (Exception ex) {
            Log.e(Log.DEFAULT_TAG, "Error!" + ex);
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static class OrderConnections extends OneToManyRelation<NewsList, CompactNews> {

    }

    public static NewsList getInstance() {

        return null;
    }

    public static NewsList getInstance(Category category) {
        return null;
    }
}



