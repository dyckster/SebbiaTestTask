package com.example.dyckster.sebbiatesttask.app.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;

import com.example.dyckster.sebbiatesttask.SebbiaTestTaskApplication;
import com.example.dyckster.sebbiatesttask.utils.InputStreamUtils;
import com.example.dyckster.sebbiatesttask.utils.Log;
import com.example.dyckster.sebbiatesttask.utils.ParseUtils;
import com.lightydev.dk.log.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by dombaev_yury on 22.12.16.
 */

@SuppressWarnings("deprecation")
public class Api {

    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    public static final int DEFAULT_READ_TIMEOUT = 30000;

    private static Executor executor = Executors.newFixedThreadPool(3);
    private static Handler handler;

    private static int nextTag;

    private static synchronized int getNextTag() {
        return nextTag++;
    }

    public static Response sendRequest(Request request) {
        int loggingTag = getNextTag();
        Logger.debug("[" + loggingTag + "] Starting request for method " + request.getMethod());
        Log.d("[" + loggingTag + "] Starting request for method " + request.getMethod());

        if (!isConnected()) {
            Logger.debug("[" + loggingTag + "] Cancelling requested: no connection.");
            Log.d("[" + loggingTag + "] Cancelling requested: no connection.");
            Response response = new Response(ServerError.NO_CONNECTION);
            return response;
        }

        InputStream input = null;
        OutputStream ostream = null;
        try {

            JSONObject json;
            String responseString;
            int responseCode;
            String url = request.getUrl();

            if (request.getMethod().getExpectedInput() == Method.ExpectedInput.MULTIPART) {
                final Pair<String, String> fileParams = request.getFileKeyAndPath();

                if (Log.LOG_ENABLED) {
                    Log.d("Executing " + request.getMethod().getHttpMethod().toString() + " request " + url);
                }

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                File file = new File(fileParams.second);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody(fileParams.first, file, ContentType.MULTIPART_FORM_DATA, fileParams.second);

                HttpEntity entity = builder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);

                responseCode = response.getStatusLine().getStatusCode();
                responseString = EntityUtils.toString(response.getEntity());
                json = new JSONObject(responseString);

            } else {
                String body = request.getBody();

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
                connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
                connection.setRequestMethod(request.getMethod().getHttpMethod().toString());


                connection.setUseCaches(false);
                connection.setDoInput(true);

                if (body != null) {
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Content-Type", request.getContentType());
                    connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes("UTF-8").length));
                    connection.setDoOutput(true);
                }

                if (body != null) {
                    ostream = new DataOutputStream(connection.getOutputStream());
                    ostream.write(body.getBytes("UTF-8"));
                }

                if (Log.LOG_ENABLED) {
                    Log.d("Executing " + request.getMethod().getHttpMethod().toString() + " request " + url + " " + body);
                }

                responseCode = connection.getResponseCode();
                if (responseCode >= 400) {
                    responseString = InputStreamUtils.toString(connection.getErrorStream());
                } else {
                    responseString = InputStreamUtils.toString(connection.getInputStream());
                }

                json = new JSONObject(responseString);

                Log.d("[" + loggingTag + "] Server responded with code " + responseCode);
                Log.logLongText(responseString);

                if (request.getMethod().getExpectedOutput() == Method.ExpectedOutput.NONE) {
                    if (responseCode == 200) {
                        return new Response(ServerError.NO_ERROR);
                    }
                }
            }
            switch (responseCode) {
                case 200:
                    String errorMessage = json.optString("message", null);
                    switch (json.getInt("code")) {
                        case 0:
                            //OK
                            if (json.names().length() != 2) {
                                throw new RuntimeException("Assertion failed: server response contains " + json.names().length() + " properties");
                            } else {

                                for (Iterator it = json.keys(); it.hasNext(); ) {
                                    String name = (String) it.next();
                                    if (!name.equals("code")) {
                                        switch (request.getMethod().getExpectedOutput()) {
                                            case JSON_OBJECT:
                                                return new Response(json.getJSONObject(name));
                                            case JSON_ARRAY:
                                                return new Response(json.getJSONArray(name));
                                            default:
                                                return new Response(ServerError.UNKNOWN_ERROR);
                                        }
                                    }


                                }

                            }
                        case 5:
                            //Unspecified internal error
                            return new Response(ServerError.UNKNOWN_ERROR, errorMessage);
                        case 14:
                            //Object \"News\" with id 111 not found
                            return new Response(ServerError.REQUEST_ERROR, errorMessage);
                        default:
                            return new Response(ServerError.UNKNOWN_ERROR, errorMessage);
                    }

                default:
                    Log.e("Request failed with status code " + responseCode + " request " + request.getUrl());
                    Log.logLongText(responseString);
                    if (json.has("error_message")) {
                        return new Response(ServerError.fromCode(json.getInt("code")), json.getString("error_message"));
                    } else {
                        if (json.has("error")) {
                            JSONObject errorJson = json.getJSONObject("error");
                            return new Response(ServerError.fromCode(errorJson.getInt("code")), errorJson.getString("error_message"));
                        } else {
                            return new Response(ServerError.UNKNOWN_ERROR);
                        }
                    }
            }

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            Logger.warn("[" + loggingTag + "] Connection timed out");
            Log.e("[" + loggingTag + "] Connection timed out" + e.getMessage());
            return new Response(ServerError.NO_CONNECTION);
        } catch (JSONException e) {
            Logger.warn("[" + loggingTag + "] Invalid json: " + e.getMessage());
            Log.e("[" + loggingTag + "] Invalid json: " + e.getMessage());

            return new Response(ServerError.UNKNOWN_ERROR);
        } catch (Exception e) {
            Logger.warn("[" + loggingTag + "] Failed to perform request " + e.getMessage());
            Log.e("[" + loggingTag + "] Failed to perform request " + e.getMessage());
            return new Response(ServerError.UNKNOWN_ERROR);
        } finally {
            try {
                if (ostream != null)
                    ostream.close();
                if (input != null)
                    input.close();
            } catch (Exception ignore) { /* ignore */ }
        }
    }

    /**
     * Connectivity check
     *
     * @return connectivity status, either true or false
     */
    private static boolean isConnected() {
        Context context = SebbiaTestTaskApplication.getInstance();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        }
        return ni.isConnected();
    }

    public interface OnRequestSentListener {
        void requestSent(Response response);
    }

    public static void sendRequestAsync(final Request request, final OnRequestSentListener listener) {

        //Comment by: dombaev_yury
        // Как это работает?
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
                final Response response = sendRequest(request);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.requestSent(response);
                        }
                    }
                });
            }
        });

    }

}
