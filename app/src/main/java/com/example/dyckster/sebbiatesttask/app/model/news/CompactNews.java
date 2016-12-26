package com.example.dyckster.sebbiatesttask.app.model.news;

import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Column;
import com.activeandroid.sebbia.annotation.Table;
import com.activeandroid.sebbia.query.Select;
import com.example.dyckster.sebbiatesttask.utils.ParseUtils;
import com.example.dyckster.sebbiatesttask.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dombaev_yury on 26.12.16.
 */
@SuppressWarnings("WeakerAccess")
@Table(name = "compact_news")
public class CompactNews extends Model {
    @Column(name = "news_id")
    int newsId;
    @Column(name = "title")
    String title;
    @Column(name = "date")
    String date;
    @Column(name = "description")
    String shortDesc;

    public String getTitle() {
        return title;
    }

    public int getNewsId() {
        return newsId;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getDate() {
        return date;
    }

    public static CompactNews fromNewsId(int newsId) {
        return new Select().from(News.class).where("news_id = ?", newsId).executeSingle();
    }


    public static CompactNews fromJson(JSONObject obj) throws JSONException {
        int id = ParseUtils.objToInt(obj.get("id"));
        CompactNews news = CompactNews.fromNewsId(id);
        if (news == null) {
            news = new CompactNews();
        }
        news.parseJson(obj);
        return news;
    }

    private void parseJson(JSONObject object) throws JSONException {
        this.newsId = ParseUtils.objToInt(object.get("id"));
        this.title = ParseUtils.objToStr(object.get("title"));
        this.shortDesc = ParseUtils.objToStr(object.get("shortDescription"));
        this.date = TimeUtil.getFormattedDate(ParseUtils.objToStr(object.get("date")));
    }
}
