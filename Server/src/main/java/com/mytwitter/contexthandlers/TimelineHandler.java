package com.mytwitter.contexthandlers;

import com.mytwitter.database.Database;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.util.GsonSingleton;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TimelineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex){
        if(ex.getRequestMethod().equals("GET")) {
            String currentUsername = (String) ex.getAttribute("username");
            List<Tweet> tweets = Database.getManager().getTimeline(currentUsername);

            OutputStream os = ex.getResponseBody();
            String json = GsonSingleton.getGson().toJson(tweets);
            try {
                ex.sendResponseHeaders(200, 0);
                os.write(json.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}