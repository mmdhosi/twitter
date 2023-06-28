package com.mytwitter.server.contexthandlers;

import com.mytwitter.server.ServerGson;
import com.mytwitter.server.database.Database;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class SearchHandler implements HttpHandler {
    Database databaseManager = Database.getManager();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        String[] segments = uri.split("/");
        String keyword = segments[2];
        //TODO: if 1 was # send a hashtag list

        ArrayList<UserProfile> searchedUsers = databaseManager.serverSearch(keyword);
        String json = ServerGson.getGson().toJson(searchedUsers);

        exchange.sendResponseHeaders(200, 0);
        OutputStream out = exchange.getResponseBody();
        out.write(json.getBytes());
        out.close();
        exchange.close();
    }
}
