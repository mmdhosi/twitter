package com.mytwitter.server;

import com.google.gson.Gson;

public class ServerGson {
    private static final Gson gson = new Gson();

    private ServerGson() {}

    public static Gson getGson() {
        return gson;
    }
}