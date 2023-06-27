package com.mytwitter.client;

import com.google.gson.*;
import com.mytwitter.tweet.*;

import java.lang.reflect.Type;

public class TweetDeserializer implements JsonDeserializer<Tweet>{

    @Override
    public Tweet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String TweetType = jsonObject.get("type").getAsString();
        switch (TweetType) {
            case "Quote" :
                return jsonDeserializationContext.deserialize(jsonObject, Quote.class);
            case "Retweet" :
                return jsonDeserializationContext.deserialize(jsonObject, Retweet.class);
            case "RegularTweet" :
                return jsonDeserializationContext.deserialize(jsonObject, RegularTweet.class);
            case "Reply" :
                return jsonDeserializationContext.deserialize(jsonObject, Reply.class);
            default :
                throw new JsonParseException("Unknown type: "+ TweetType);
        }
    }
}