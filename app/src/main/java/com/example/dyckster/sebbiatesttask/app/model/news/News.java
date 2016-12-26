package com.example.dyckster.sebbiatesttask.app.model.news;

import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Column;
import com.activeandroid.sebbia.annotation.Table;
import com.activeandroid.sebbia.query.Select;
import com.example.dyckster.sebbiatesttask.utils.ParseUtils;
import com.example.dyckster.sebbiatesttask.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "news_table")
public class News extends Model {
    @Column(name = "news_id")
     long newsId;
    @Column(name = "news_title")
     String title;
    @Column(name = "full_desc")
     String fullDescription;
    @Column(name = "short_desc")
     String shortDescription;
    @Column(name = "date")
     String date;

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long id) {
        this.newsId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ClassPojo [newsId = " + newsId + ", title = " + title + ", fullDescription = " + fullDescription + ", shortDescription = " + shortDescription + ", date = " + date + "]";
    }

    public static News fromNewsId(long newsId) {
        return new Select().from(News.class).where("news_id = ?", newsId).executeSingle();
    }

    public static News fromJson(JSONObject jsonObject) throws JSONException {
        long id = ParseUtils.objToLong(jsonObject.get("id"));
        News news = News.fromNewsId(id);
        if (news == null) {
            news = new News();
        }
        news.parseJson(jsonObject);
        return news;
    }

    private void parseJson(JSONObject object) throws JSONException {
        this.newsId = ParseUtils.objToLong(object.get("id"));
        this.title = ParseUtils.objToStr(object.get("title"));
        this.shortDescription = ParseUtils.objToStr(object.get("shortDescription"));
        this.shortDescription = ParseUtils.objToStr(object.get("shortDescription"));
        this.fullDescription = ParseUtils.objToStr(object.get("fullDescription"));
        this.date = TimeUtil.getFormattedDate(ParseUtils.objToStr(object.get("date")));
    }
}