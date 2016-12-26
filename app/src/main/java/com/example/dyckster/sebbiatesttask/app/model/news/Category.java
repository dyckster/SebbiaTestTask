package com.example.dyckster.sebbiatesttask.app.model.news;

import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Column;
import com.activeandroid.sebbia.annotation.Table;
import com.activeandroid.sebbia.query.Select;
import com.example.dyckster.sebbiatesttask.utils.ParseUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "category_table")
public class Category extends Model {
    @Column(name = "category_id")
    int categoryId;
    @Column(name = "category_name")
    String name;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Category fromId(int id) {
        return new Select().from(Category.class).where("category_id = ?", id).executeSingle();
    }

    public static Category fromJson(JSONObject obj) throws JSONException {
//        int id = ParseUtils.objToInt(obj.get("id"));
//        Category category = Category.fromId(id);
//        if (category == null) {
           Category category = new Category();
//        }
        category.parseJson(obj);
        return category;
    }


    private void parseJson(JSONObject obj) throws JSONException {
        this.categoryId = ParseUtils.objToInt(obj.get("id"));
        this.name = ParseUtils.objToStr(obj.get("name"));
        this.save();
    }
}
