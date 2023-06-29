package com.mytwitter.server.contexthandlers;

import com.google.gson.Gson;
import com.mytwitter.server.database.Database;
import com.mytwitter.server.ServerGson;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class ProfileHandler implements HttpHandler {
    Database databaseManager = Database.getManager();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String usernameToRequest = (String) exchange.getAttribute("username");
        Gson gson = ServerGson.getGson();
        if(exchange.getRequestMethod().equals("GET")){
            InputStream in = exchange.getRequestBody();

            String requestUri = exchange.getRequestURI().toString();
            String[] segments = requestUri.split("/");
            String usernameToView = segments[2];
//            String usernameToRequest = segments[3];
            UserProfile userProfile = new UserProfile();
            User user = databaseManager.getUser(usernameToView);

            if(user != null) {
                ArrayList<User> followers=databaseManager.getFollowers(usernameToView);
                ArrayList<User> followings=databaseManager.getFollowings(usernameToView);
                ArrayList<UserProfile> followersProfile = null;
                ArrayList<UserProfile> followingsProfile = null;
                for (User u:followers) {
                    UserProfile userProfile1=new UserProfile();
                    userProfile1.setAvatar(databaseManager.getAvatar(u.getUserName()));
                    followersProfile.add(userProfile1);
                }
                for (User u:followings) {
                    UserProfile userProfile1=new UserProfile();
                    userProfile1.setAvatar(databaseManager.getAvatar(u.getUserName()));
                    followingsProfile.add(userProfile1);
                }

                userProfile.setUser(user);
                userProfile.setAvatar(databaseManager.getAvatar(usernameToView));
                userProfile.setHeader(databaseManager.getHeader(usernameToView));
                userProfile.setCountFollowers(followers.size());
                userProfile.setCountFollowings(followings.size());
                userProfile.setTweets((ArrayList<Tweet>) databaseManager.getTweetsForUser(usernameToView));
                userProfile.setBlocked(databaseManager.checkBlocked(usernameToView,usernameToRequest));
                userProfile.setFollowed(databaseManager.checkFollowed(usernameToView,usernameToRequest));
                userProfile.setFollowers(followersProfile);
                userProfile.setFollowings(followingsProfile);


                exchange.sendResponseHeaders(404, 0);
                OutputStream out = exchange.getResponseBody();
                out.write(gson.toJson(userProfile).getBytes());
                out.close();
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
            exchange.close();
        }
    }
}
