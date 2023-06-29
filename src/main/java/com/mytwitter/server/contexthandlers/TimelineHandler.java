package com.mytwitter.server.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.server.database.Database;
import com.mytwitter.server.ServerGson;
import com.mytwitter.tweet.Reply;
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

            String requestUri = exchange.getRequestURI().toString();
            String[] segments = requestUri.split("/");
            String action = segments[2];

            String currentUsername = (String) exchange.getAttribute("username");
            String json = null;
            int rCode = 404;

            if(action.equals("timeline")) {
                List<Tweet> tweets = Database.getManager().getTimeline(currentUsername);
                json = ServerGson.getGson().toJson(tweets);
                rCode = 200;
            } else if(action.equals("comments")) {
                List<Reply> replies = Database.getManager().getReplies(currentUsername, Integer.parseInt(segments[3]));
                json = ServerGson.getGson().toJson(replies);
                rCode = 200;
            }
            try {
                if(rCode == 404){
                    exchange.sendResponseHeaders(404,0);
                } else {
                    exchange.sendResponseHeaders(200,0);
                    OutputStream os = exchange.getResponseBody();
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