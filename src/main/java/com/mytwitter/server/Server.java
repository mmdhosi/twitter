package com.mytwitter.server;

import com.google.gson.Gson;
import com.mytwitter.server.contexthandlers.*;
import com.mytwitter.server.database.Database;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.net.httpserver.Authenticator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final static String DEFAULT_REALM = "Restricted area";

    static ExecutorService clientsThreads = Executors.newCachedThreadPool();
    static Database databaseManager = Database.getManager();
    static Gson gson = new Gson();

    public static void main(String[] args) {
        log.info("Server starting..");
        HttpServer server = null;


        // Setting up jwt authentication
        Authenticator auth = new Authenticator() {
            @Override
            public Result authenticate(HttpExchange exch) {
                // check for authorization header
                String jwtToken = exch.getRequestHeaders().getFirst("Authorization");
                if (jwtToken == null) {
                    return new Failure(401);
                }

                // validate authorization header
                Jws<Claims> claims;
                try {
                    // Validate the JWT token
                    claims = JwtManager.parseJwt(jwtToken);
                } catch (JwtException e) {
                    return new Failure(401);
                }

                // get the username from the jwt claims
                String username = (String) claims.getBody().get("username");

                // set username attribute in the exchange
                exch.setAttribute("username", username);

                // authentication successful
                HttpPrincipal authenticatedUser = new HttpPrincipal(username, DEFAULT_REALM);
                return new Success(authenticatedUser);
            }
        };

        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            log.info("Initializing contexts..");
            server.createContext("/signup", new SignupHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/home", new TimelineHandler()).setAuthenticator(auth);
            server.createContext("/user", new ProfileHandler());
            server.createContext("/users", new UsersHandler()).setAuthenticator(auth);
            server.createContext("/search", new SearchHandler());
            server.createContext("/hashtag", new HashtagHandler());

            //TODO: coontext hashtag

            server.setExecutor(clientsThreads);
            server.start();
            log.info("Server started");
        } catch (IOException e) {
            log.error("Server couldn't start!", e);
        }
//        databaseManager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));
//        Thread clientHandler=new ClientHandler(databaseManager.getUser("mmd"));
//        clientHandler.start();



//        manager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));

    }
}
