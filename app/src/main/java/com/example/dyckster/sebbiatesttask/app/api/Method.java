package com.example.dyckster.sebbiatesttask.app.api;

import com.example.dyckster.sebbiatesttask.BuildConfig;

import java.util.Locale;

/**
 * Created by dyckster on 14.11.2016.
 */

public enum Method {
    GET_CATEGORIES(HttpMethod.GET, "/v1/news/categories", ExpectedInput.NONE, ExpectedOutput.JSON_ARRAY),
    GET_NEWS_LIST(HttpMethod.GET, "/v1/news/categories/%s/news", ExpectedInput.NONE, ExpectedOutput.JSON_ARRAY),
    GET_DETAILS(HttpMethod.GET, "/v1/news/details/", ExpectedInput.NONE, ExpectedOutput.JSON_OBJECT);

    public static enum ExpectedInput {
        JSON_OBJECT,
        JSON_ARRAY,
        MULTIPART,
        NONE
    }

    public static enum ExpectedOutput {
        JSON_OBJECT,
        JSON_ARRAY,
        MULTIPART,
        NONE
    }


    public enum HttpMethod {
        GET,
        POST,
        PATCH,
        DELETE
    }

    private String path;
    private HttpMethod httpMethod;
    private ExpectedInput expectedInput;
    private ExpectedOutput expectedOutput;

    Method(HttpMethod httpMethod, String path, ExpectedInput input, ExpectedOutput outPut) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.expectedInput = input;
        this.expectedOutput = outPut;
    }

    public ExpectedOutput getExpectedOutput() {
        return expectedOutput;
    }

    public ExpectedInput getExpectedInput() {
        return expectedInput;
    }

    public String getUrl() {
        return getUrl(null);
    }

    public String getUrl(String[] urlParams) {
        return getUrl(urlParams, false);
    }

    public String getUrl(String[] urlParams, boolean google) {


        if (google) {
            return String.format(Locale.US, path, urlParams);
        } else {
            return String.format(Locale.US, BuildConfig.BASE_URL + path, urlParams);
        }
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean isHttpMethod(HttpMethod httpMethod) {
        return this.getHttpMethod() == httpMethod;
    }
}