package com.example.dyckster.sebbiatesttask.model;

import java.util.ArrayList;

public class CategoriesList {
    private ArrayList<Category> list;

    private String code;

    public ArrayList<Category> getList() {
        return list;
    }

    public void setList(ArrayList<Category> list) {
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


