package com.mytwitter.util;

public enum OutputType {
    INVALID_EMAIL, SUCCESS, FAILURE, DUPLICATE_USERNAME, DUPLICATE_EMAIL, DUPLICATE_PHONENUMBER, INVALID_PASSWORD,NOT_FOUND, INVALID, UNAUTHORIZED, UNKNOWN;
    public static OutputType parseCode(int statusCode){
        return switch (statusCode) {
            case 200 -> SUCCESS;
            case 400 -> INVALID;
            case 401 -> UNAUTHORIZED;
            case 404 -> NOT_FOUND;
            default -> UNKNOWN;
        };
    }

}
