package com.mytwitter.server.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.server.database.Database;
import com.mytwitter.util.OutputType;
import com.mytwitter.user.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignupHandler implements HttpHandler {

    public static OutputType signup(User user){
//        String regex = "^(.+)@(.+)$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        if(!matcher.matches())
//            return OutputType.INVALID_EMAIL;

        return Database.getManager().addUser(user);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("POST")) {
            Gson gson = new Gson();
            InputStream in = exchange.getRequestBody();
            byte[] request = in.readAllBytes();
            in.close();
            String requestJson = new String(request);
            User user = gson.fromJson(requestJson, User.class);
            OutputType output = signup(user);
            OutputStream os = exchange.getResponseBody();

            if(output.equals(OutputType.DUPLICATE_USERNAME)){
                exchange.sendResponseHeaders(409, 0);
                os.write("Username already exists".getBytes());
            } else if(output.equals(OutputType.DUPLICATE_EMAIL)){
                exchange.sendResponseHeaders(409, 0);
                os.write("Email already exists".getBytes());
            }else if(output.equals(OutputType.DUPLICATE_PHONENUMBER)){
                exchange.sendResponseHeaders(409, 0);
                os.write("Phone number already exists".getBytes());
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
            os.close();
            exchange.close();
        }
    }
}