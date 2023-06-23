package com.mytwitter.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.database.Database;
import com.mytwitter.util.GsonSingleton;
import com.mytwitter.util.OutputType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class UsersHandler implements HttpHandler {
    Database dbManager = Database.getManager();
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String username = (String) exchange.getAttribute("username");
        Gson gson = GsonSingleton.getGson();
        String uri = exchange.getRequestURI().toString();
        // sample uri /users/follow/mmd
        String[] segments = uri.split("/");

        if (segments.length != 4) {
            exchange.sendResponseHeaders(400, 0);
        }

        String requestedUsername = segments[3];

        // do the operation
        OutputType result = OutputType.INVALID;
        if(segments[2].equals("follow")) {
            if(exchange.getRequestMethod().equals("POST"))
                result = dbManager.addFollower(requestedUsername, username);
            else if(exchange.getRequestMethod().equals("DELETE"))
                result = dbManager.removeFollower(requestedUsername, username);
        }
        else if(segments[2].equals("block")) {
            if(exchange.getRequestMethod().equals("POST"))
                result = dbManager.block(requestedUsername, username);
            else if(exchange.getRequestMethod().equals("DELETE"))
                result = dbManager.unblock(requestedUsername, username);
        }

        // check result
        if (result == OutputType.NOT_FOUND) {
            exchange.sendResponseHeaders(404, 0);
        } else if (result == OutputType.INVALID) {
            exchange.sendResponseHeaders(400, 0);
        } else if (result == OutputType.SUCCESS) {
            exchange.sendResponseHeaders(200, 0);
        }
    }
}
