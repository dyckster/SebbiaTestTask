package com.example.dyckster.sebbiatesttask.app.model.news;

import com.activeandroid.sebbia.ActiveAndroid;
import com.activeandroid.sebbia.model.OneToManyRelation;
import com.activeandroid.sebbia.query.Select;
import com.example.dyckster.sebbiatesttask.app.api.Method;
import com.example.dyckster.sebbiatesttask.app.api.Request;
import com.example.dyckster.sebbiatesttask.app.api.Response;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableList;
import com.example.dyckster.sebbiatesttask.app.model.UpdatableModel;
import com.example.dyckster.sebbiatesttask.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: 23.12.16 Change to updlist
public class CategoriesList extends UpdatableList<Category> {

    private List<Category> categories;

    @Override
    public List<Category> getItems() {
        return categories;
    }


    public static class CategoryConnections extends OneToManyRelation<CategoriesList, Category> {
    }

    public static CategoriesList getInstance() {
        synchronized (CategoriesList.class) {
            CategoriesList categoriesList;
            categoriesList = new Select().from(CategoriesList.class).executeSingle();
            if (categoriesList == null) {
                categoriesList = new CategoriesList();
                categoriesList.categories = new ArrayList<>();
                categoriesList.save();
            } else {
                categoriesList.categories = new ArrayList<>(CategoryConnections.getRelations(CategoryConnections.class, categoriesList));
            }
            return categoriesList;
        }
    }


    public List<Category> getCategories() {
        return categories;
    }

    @Override
    protected long getUpdatePeriod() {
        return TimeUnit.HOURS.toMillis(1);
    }

    @Override
    protected Request getUpdateRequest() {
        return new Request(Method.GET_CATEGORIES);
    }

    @Override
    protected void parseAndSave(Response response) throws JSONException {
        try {
            synchronized (CategoriesList.class) {
                ActiveAndroid.beginTransaction();
                JSONArray array = response.getJsonArray();
                List<Category> categories = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    categories.add(Category.fromJson(array.getJSONObject(i)));
                }
                this.categories = categories;
                this.save();
                CategoryConnections.setRelations(CategoryConnections.class, CategoriesList.getInstance(), categories);
                ActiveAndroid.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e(Log.DEFAULT_TAG + "Exception", e);
        } finally {
        ActiveAndroid.endTransaction();
        }
    }


}


