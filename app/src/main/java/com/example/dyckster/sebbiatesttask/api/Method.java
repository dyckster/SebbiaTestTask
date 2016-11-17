package com.example.dyckster.sebbiatesttask.api;

/**
 * Created by dyckster on 14.11.2016.
 */

public enum Method {
    GET_CATEGORIES(HttpMethod.GET, "/v1/news/categories"),
    GET_NEWS_LIST(HttpMethod.GET, "/v1/news/categories/%s/news"),
    GET_DETAILS(HttpMethod.GET, "/v1/news/details?id=%s");

    private String path;
    private HttpMethod httpMethod;

    Method(HttpMethod httpMethod, String path) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}