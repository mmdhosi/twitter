package com.mytwitter.server.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.server.database.Database;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.OutputType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class EditHandler implements HttpHandler {
    public static OutputType editProfile(UserProfile userProfile){

        return Database.getManager().editProfile(userProfile);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("POST")) {
            String usernameToRequest = (String) exchange.getAttribute("username");
            Gson gson = new Gson();
            InputStream in = exchange.getRequestBody();
            byte[] request = in.readAllBytes();
            in.close();
            String requestJson = new String(request);
            UserProfile userProfile = gson.fromJson(requestJson, UserProfile.class);
            if ((!Objects.equals(userProfile.getUser().getUserName(), usernameToRequest))){
                System.out.println("INVALID");
                exchange.close();
                return;
            }
            OutputType output = editProfile(userProfile);
            OutputStream os = exchange.getResponseBody();

            exchange.sendResponseHeaders(200, 0);

            os.close();
            exchange.close();
        }
    }
}
