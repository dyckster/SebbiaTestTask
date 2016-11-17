package com.example.dyckster.sebbiatesttask.api;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class RestCall {
    private final static String API_LINK = "http://testtask.sebbia.com";

    public static void apiCall(Method method, @Nullable String extraParams, Map<String, String> bodyParams, final OnRestCallListener listener) {
        Builders.Any.B builder = Ion.with(SebbiaTestTaskApplication.getAppContext())
                .load(method.getHttpMethod().toString(), API_LINK + formattedPath(method.getPath(), extraParams));

        //Set Body
        if (bodyParams != null) {
            for (String body : bodyParams.keySet()) {
                builder.addQuery(body, bodyParams.get(body));
            }
        }
        builder.asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (result != null) {
                            int code = result.getHeaders().code();
                            try {
                                JSONObject obj = new JSONObject(result.getResult());
                                if (code < 400) {
                                    listener.onSuccess(obj);
                                } else {
                                    listener.onError();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            listener.onConnectionError();
                        }
                    }
                });
    }


    public interface OnRestCallListener {

        void onSuccess(JSONObject object) throws JSONException;

        void onError();

        void onConnectionError();
    }

    private static String formattedPath(String path, String param) {
        if (param == null && !path.contains("%s")) {
            return path;
        } else if (param == null && path.contains("%s")) {
            Log.e("RestCall", "No params were passed!");
            return path;
        } else if (param != null && path.contains("%s")) {
            return String.format(path, param);
        } else if (param != null && path.contains("%s")) {
            Log.e("RestCall", "Requested path requires params!");
            return path;
        }
        return null;
    }
}
