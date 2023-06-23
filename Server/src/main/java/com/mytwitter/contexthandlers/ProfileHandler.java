package com.mytwitter.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.database.Database;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.GsonSingleton;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProfileHandler implements HttpHandler {
    Database databaseManager = Database.getManager();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = GsonSingleton.getGson();
        if(exchange.getRequestMethod().equals("GET")){
            InputStream in = exchange.getRequestBody();

            String requestUri = exchange.getRequestURI().toString();
            String[] segments = requestUri.split("/");
            String username = segments[segments.length-1];
            UserProfile userProfile = new UserProfile();
            User user = databaseManager.getUser(username);

            if(user != null) {
                userProfile.setUser(user);
                userProfile.setAvatar(databaseManager.getAvatar(username));
                userProfile.setHeader(databaseManager.getHeader(username));
                userProfile.setTweets((ArrayList<Tweet>) databaseManager.getTweetsForUser(username));

                exchange.sendResponseHeaders(404, 0);
                OutputStream out = exchange.getResponseBody();
                out.write(gson.toJson(userProfile).getBytes());
                out.close();
            } else {
                exchange.sendResponseHeaders(404, 0);
            }

        }
    }
}
