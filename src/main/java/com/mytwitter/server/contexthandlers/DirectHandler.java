package com.mytwitter.server.contexthandlers;

import com.mytwitter.direct.Direct;
import com.mytwitter.direct.Message;
import com.mytwitter.server.ServerGson;
import com.mytwitter.server.database.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DirectHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        String[] segments = url.split("/");
        Database databaseManager = Database.getManager();
        String requesterUsername = (String) exchange.getAttribute("username");
        String requestMethod = exchange.getRequestMethod();

        // check the request
        try {
            if(segments[2].equalsIgnoreCase("all")){
                // get all the users that this user has history with
                if(requestMethod.equalsIgnoreCase("GET")) {
                    ArrayList<Direct> directs = databaseManager.getDirect(requesterUsername);
                    String json = ServerGson.getGson().toJson(directs);

                    exchange.sendResponseHeaders(200, 0);
                    OutputStream out = exchange.getResponseBody();
                    out.write(json.getBytes());
                    out.close();
                }
            } else if(segments[2].equals("messages")){
                // get all the messages between this user and a specific user
                if(requestMethod.equalsIgnoreCase("GET")) {
                    ArrayList<Message> messages = databaseManager.getAllMessage(requesterUsername, segments[3]);

                    String json = ServerGson.getGson().toJson(messages);
                    exchange.sendResponseHeaders(200, 0);
                    OutputStream out = exchange.getResponseBody();
                    out.write(json.getBytes());
                    out.close();
                }
            } else if(segments[2].equals("seen")){
                // set a message seen
                if(requestMethod.equalsIgnoreCase("POST")){
                    databaseManager.setSeen(Integer.parseInt(segments[3]));
                    exchange.sendResponseHeaders(200,0);
                }
            } else if(segments[2].equalsIgnoreCase("send_message")){
                // send a message
                String receiver = segments[3];
                String content = new String(exchange.getRequestBody().readAllBytes());
                databaseManager.addMessage(requesterUsername, receiver, content);
                exchange.sendResponseHeaders(200, 0);
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
