package com.mytwitter.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mytwitter.tweet.Tweet;

public class ClientGson {
    private static final Gson gson = new Gson();

    private static final Gson timelineGson = new GsonBuilder()
            .registerTypeAdapter(Tweet.class, new TweetDeserializer())
            .create();

    private ClientGson() {}

    public static Gson getGson() {
        return gson;
    }
    public static Gson getTimelineGson(){
        return timelineGson;
    }

}