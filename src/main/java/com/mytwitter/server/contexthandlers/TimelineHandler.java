package com.mytwitter.server.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.server.database.Database;
import com.mytwitter.server.ServerGson;
import com.mytwitter.tweet.Tweet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TimelineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        if(exchange.getRequestMethod().equals("GET")) {
            String currentUsername = (String) exchange.getAttribute("username");
            List<Tweet> tweets = Database.getManager().getTimeline(currentUsername);
            try {
                if(tweets == null){
                    exchange.sendResponseHeaders(404,0);
                } else {
                    exchange.sendResponseHeaders(200,0);
                    OutputStream os = exchange.getResponseBody();
                    String json = ServerGson.getGson().toJson(tweets);

                    os.write(json.getBytes());
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            exchange.close();
        }
    }
}