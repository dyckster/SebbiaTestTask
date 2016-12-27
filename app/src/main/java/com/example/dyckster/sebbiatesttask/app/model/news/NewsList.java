package com.example.dyckster.sebbiatesttask.app.model.news;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.activeandroid.sebbia.ActiveAndroid;
import com.activeandroid.sebbia.annotation.Column;
import com.activeandroid.sebbia.annotation.Table;
import com.activeandroid.sebbia.model.OneToManyRelation;
import com.activeandroid.sebbia.query.Select;
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
import java.util.Collections;
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

    Category category;
    @Column(name = "categoryId")
    int categoryId;

    public NewsList(Category category) {
        this.category = category;
        this.categoryId = category.getCategoryId();
    }

    public List<CompactNews> getList() {
        return news;
    }

    public NewsList() {
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
        if (news == null) {
            return Collections.emptyList();
        } else {
            return news;
        }
    }

    @Override
    protected long getUpdatePeriod() {
        return TimeUnit.MINUTES.toMillis(5);
    }


    private Request getRequest(int nextPage) throws JSONException {
        Map<String, String> param = new HashMap<>();
        param.put("page", String.valueOf(nextPage));
        List<String> urlParams = new ArrayList<>();
        urlParams.add(String.valueOf(categoryId));
        return new Request(Method.GET_NEWS_LIST, urlParams, param);
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
            NewsConnection.setRelations(NewsConnection.class, NewsList.this, this.news);
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(Log.DEFAULT_TAG, "Error!" + ex);
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static class NewsConnection extends OneToManyRelation<NewsList, CompactNews> {

    }


    public static NewsList getInstance(Category category) {
        NewsList newsList = null;

        newsList = fromCategory(category);

        if (newsList == null) {
            newsList = new NewsList(category);
            newsList.save();
        } else {
            newsList.news = NewsConnection.getRelations(NewsConnection.class, newsList);
        }

        return newsList;
    }

    private static NewsList fromCategory(Category category) {
        return new Select().from(NewsList.class).where("categoryId = ?", category.getCategoryId()).executeSingle();
    }
}



