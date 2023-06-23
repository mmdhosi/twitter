package com.mytwitter.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.util.OutputType;
import com.mytwitter.database.Database;
import com.mytwitter.server.JwtManager;
import com.mytwitter.user.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class LoginHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public static OutputType login(User userData){

        User user = Database.getManager().getUser(userData.getUserName());
        if(user!=null){
            if(Objects.equals(user.getPassword(), userData.getPassword())){
                return OutputType.SUCCESS;
            }
            return OutputType.INVALID_PASSWORD;
        }
        return OutputType.NOT_FOUND;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        if (exchange.getRequestMethod().equals("POST")) {
            InputStream in = exchange.getRequestBody();
            byte[] request = in.readAllBytes();
            in.close();
            String requestJson = new String(request);
            User user = gson.fromJson(requestJson, User.class);
            OutputType loginResult = login(user);

            Instant now = Instant.now();

            OutputStream os = exchange.getResponseBody();

            if(loginResult.equals(OutputType.NOT_FOUND)){
                exchange.sendResponseHeaders(404, 0);
                os.write("User doesn't exist.".getBytes());
            } else if(loginResult.equals(OutputType.INVALID_PASSWORD)){
                exchange.sendResponseHeaders(401, 0);
                os.write("User password is invalid".getBytes());
            } else {
                // build user jwt
                String jwt = Jwts.builder()
                        .claim("username", user.getUserName())
                        .setSubject(user.getUserName())
                        .setId(UUID.randomUUID().toString())
                        .setIssuedAt(Date.from(now))
                        .signWith(JwtManager.getHmacKey())
                        .compact();

                exchange.getResponseHeaders().add("authorization", jwt);
                exchange.sendResponseHeaders(200, 0);
                log.info("Client login: "+user.getUserName());
            }
            os.close();
        }
    }
}
