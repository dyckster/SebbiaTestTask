package com.example.dyckster.sebbiatesttask.model;

import java.util.ArrayList;


public class NewsList {

    private ArrayList<NewsListItem> list;

    private String code;

    public ArrayList<NewsListItem> getList() {
        return list;
    }

    public void setList(ArrayList<NewsListItem> list) {
        this.list = list;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ClassPojo [list = " + list + ", code = " + code + "]";
    }
}



