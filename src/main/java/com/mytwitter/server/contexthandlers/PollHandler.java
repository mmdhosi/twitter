package com.mytwitter.server.contexthandlers;

import com.mytwitter.direct.Direct;
import com.mytwitter.direct.Message;
import com.mytwitter.server.ServerGson;
import com.mytwitter.server.database.Database;
import com.mytwitter.util.OutputType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PollHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        String[] segments = url.split("/");
        Database databaseManager = Database.getManager();
        String requesterUsername = (String) exchange.getAttribute("username");
        String requestMethod = exchange.getRequestMethod();

        // check the request
        try {
            if(segments[2].equalsIgnoreCase("answer")){
                if(requestMethod.equalsIgnoreCase("POST")) {
                    OutputType out = databaseManager.setPollAnswer(Integer.parseInt(segments[3]), requesterUsername);
                    if(out!=OutputType.SUCCESS)
                        exchange.sendResponseHeaders(400,0);
                    else
                        exchange.sendResponseHeaders(200,0);
                }
            } else {
                exchange.sendResponseHeaders(400,0);
            }

        } catch (IndexOutOfBoundsException|NumberFormatException|NullPointerException e){
            e.printStackTrace();
            exchange.sendResponseHeaders(400,0);
        }
        exchange.close();
    }
}
