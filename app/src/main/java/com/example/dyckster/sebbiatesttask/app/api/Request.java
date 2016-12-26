package com.example.dyckster.sebbiatesttask.app.api;

import android.support.v4.util.Pair;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Request {
    private final Method method;
    private final List<String> urlPathnameParams;
    private final Map<String, String> params;
    private final JSONObject jsonData;
    private final Pair<String, String> fileKeyAndPath;
    private String accessToken;


    public static Map<String, String> params(String... args) {
        if (args.length % 2 == 1) {
            throw new IllegalArgumentException("Number of params myst be even");
        }

        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            params.put(args[i], args[i + 1]);
        }
        return params;
    }


    public Request(Method method) {
        this(method, null, null, null);
    }

    public Request(Method method, Map<String, String> params) {
        this(method, null, params, null);
    }

    public Request(Method method, List<String> urlPathnameParams) {
        this(method, urlPathnameParams, null, null);
    }

    public Request(Method method, JSONObject jsonData) {
        this(method, null, null, jsonData);
    }

    public Request(Method method, List<String> urlPathnameParams, Map<String, String> params, JSONObject jsonData) {
        this(method, urlPathnameParams, params, jsonData, null);
    }

    public Request(Method method, List<String> urlPathnameParams, Map<String, String> params, JSONObject jsonData, Pair<String, String> fileKeyAndPath) {
        //this.accessToken = API.getAccessToken();
        this.method = method;
        this.params = params;
        this.urlPathnameParams = urlPathnameParams;
        this.jsonData = jsonData;
        this.fileKeyAndPath = fileKeyAndPath;
    }

    public Pair<String, String> getFileKeyAndPath() {
        return fileKeyAndPath;
    }

    public Request(Method method, Pair<String, String> fileUri) {
        this(method, null, null, null, fileUri);
    }

    public String getContentType() {
        if (method.getExpectedInput() == Method.ExpectedInput.JSON_OBJECT) {
            return "application/json";
        } else if (method.getExpectedInput() == Method.ExpectedInput.MULTIPART) {
            return "multipart/from-data";
        } else {
            return "application/x-www-form-urlencoded";
        }
    }

    Method getMethod() {
        return method;
    }

    Map<String, String> getParams() {
        return params;
    }

    List<String> getUrlPathnameParams() {
        return urlPathnameParams;
    }

    private String paramsToString(Map<String, String> params) {
        try {
            StringBuilder paramsStr = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (paramsStr.length() > 0) {
                    paramsStr.append("&");
                }
                paramsStr.append(param.getKey());
                paramsStr.append("=");
                paramsStr.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }
            return paramsStr.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    String getUrl() {
        StringBuilder url = new StringBuilder();

        if (this.urlPathnameParams == null) {
            url.append(method.getUrl());
        } else {
            url.append(method.getUrl(this.urlPathnameParams.toArray(new String[]{})));
        }


//        if (!method.equals(Method.AUTH_API_TOKEN) &&
//                !method.equals(Method.GET_GOOGLE_DIRECTIONS) &&
//                !method.equals(Method.POST_USER_REMIND_PASSWORD) &&
//                !method.equals(Method.POST_USER_REGISTRATION)) {
//            Map<String, String> token = new HashMap<>();
//            token.put("access_token", accessToken);
//            url.append("?");
//            url.append(paramsToString(token));
//        } else {
        url.append("?");
//        }

//        boolean allowedPostWithParams = (method.equals(Method.POST_ORDER_START_TRACKING) ||
//                method.equals(Method.POST_ORDER_STOP_TRACKING) ||
//                method.equals(Method.SEND_USER_CHECKING));


        if ((method.isHttpMethod(Method.HttpMethod.GET)
                //        || allowedPostWithParams
        )) {
            if (params != null) {
                url.append("&");
                url.append(paramsToString(params));
            }
        }
        return url.toString();
    }

    String getBody() {
        if (method.getExpectedInput() == Method.ExpectedInput.JSON_OBJECT) {
            return jsonData.toString();
        } else if (!method.isHttpMethod(Method.HttpMethod.GET) && params != null && params.size() > 0) {
            return paramsToString(params);
        } else {
            return null;
        }
    }
}

