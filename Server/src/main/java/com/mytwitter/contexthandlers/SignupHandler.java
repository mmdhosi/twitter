package com.mytwitter.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.util.OutputType;
import com.mytwitter.database.Database;
import com.mytwitter.user.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SignupHandler implements HttpHandler {

    public static OutputType signup(User user){
        //TODO: email and phone both should not be null
        //TODO: transfer email validation to client

//        String regex = "^(.+)@(.+)$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        if(!matcher.matches())
//            return OutputType.INVALID_EMAIL;

        OutputType out = Database.getManager().addUser(user);

        return OutputType.SUCCESS;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        if(t.getRequestMethod().equals("POST")) {
            Gson gson = new Gson();
            InputStream in = t.getRequestBody();
            byte[] request = in.readAllBytes();
            in.close();
            String requestJson = new String(request);
            User user = gson.fromJson(requestJson, User.class);
            OutputType output = signup(user);
            //TODO: JWT
            OutputStream os = t.getResponseBody();

            if(output.equals(OutputType.DUPLICATE_USERNAME)){
                t.sendResponseHeaders(409, 0);
                os.write("Username already exists".getBytes());
            } else if(output.equals(OutputType.DUPLICATE_EMAIL)){
                t.sendResponseHeaders(409, 0);
                os.write("Email already exists".getBytes());
            }else if(output.equals(OutputType.DUPLICATE_PHONENUMBER)){
                t.sendResponseHeaders(409, 0);
                os.write("Phone number already exists".getBytes());
            } else {
                t.sendResponseHeaders(200, 0);
            }
            os.close();
        }
    }
}