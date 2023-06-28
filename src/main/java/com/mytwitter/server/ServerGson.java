package com.mytwitter.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.util.TweetDeserializer;

public class ServerGson {
    private static final Gson gson = new Gson();
    private static final Gson tweetGson = new GsonBuilder()
            .registerTypeAdapter(Tweet.class, new TweetDeserializer())
            .create();

    private ServerGson() {}

    public static Gson getGson() {
        return gson;
    }
    public static Gson getTweetGson(){
        return tweetGson;
    }

}