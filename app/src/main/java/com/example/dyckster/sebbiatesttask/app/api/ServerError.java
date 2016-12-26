package com.example.dyckster.sebbiatesttask.app.api;

/**
 * Created by dombaev_yury on 22.12.16.
 */

public enum ServerError {
    NO_ERROR(200),
    NO_CONNECTION(0),
    REQUEST_ERROR(0),
    UNKNOWN_ERROR(1),
    INVALID_BID_PRICE(400),
    INVALID_TOKEN(401),
    INVALID_PASSWORD(402),
    ACCESS_DENIED(403),
    SERVICE_ERROR(500);

    private int code;

    public int getCode() {
        return code;
    }

    private ServerError(int code) {
        this.code = code;
    }

    public static ServerError fromCode(int code) {
        for (ServerError er : ServerError.values()) {
            if (er.getCode() == code) {
                return er;
            }
        }
        return ServerError.REQUEST_ERROR;
    }

}
