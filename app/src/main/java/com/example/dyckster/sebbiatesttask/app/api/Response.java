package com.example.dyckster.sebbiatesttask.app.api;



import org.json.JSONArray;
import org.json.JSONObject;

public class Response {

    private ServerError errorCode;
    private String errorMessage;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    public Response(ServerError errorCode) {
        this(errorCode, null);
    }

    public Response(ServerError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        if (errorMessage == null) {
            switch (errorCode) {
                case UNKNOWN_ERROR:
                    this.errorMessage = "Data error";
                    break;
                case NO_CONNECTION:
                    this.errorMessage = "No connection error";
                    break;
            }
        } else {
            this.errorMessage = errorMessage;
        }
        this.jsonObject = null;
    }

    public Response(JSONObject jsonObject) {
        this.errorCode = ServerError.NO_ERROR;
        this.errorMessage = null;
        this.jsonObject = jsonObject;
    }

    public Response(JSONArray jsonArray) {
        this.errorCode = ServerError.NO_ERROR;
        this.errorMessage = null;
        this.jsonArray = jsonArray;
    }

    public boolean isSuccessful() {
        return errorCode == ServerError.NO_ERROR;
    }

    public ServerError getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }


}

