package com.mytwitter.server.contexthandlers;

import com.mytwitter.server.ServerGson;
import com.mytwitter.server.database.Database;
import com.mytwitter.tweet.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;

public class TweetHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(UsersHandler.class);
    Database manager = Database.getManager();

    private RequestTweet getTweet(InputStream in) throws IOException {
        RequestTweet result = ServerGson.getGson().fromJson(new String(in.readAllBytes()), RequestTweet.class);
        in.close();
        return result;
    }

    private String getNewId(){
        try {
            FileInputStream in = new FileInputStream("tweetImages/last_id.txt");
            int lastId = Integer.parseInt(new String(in.readAllBytes()));
            in.close();
            String newId = (""+(lastId+1));

            FileOutputStream out = new FileOutputStream("tweetImages/last_id.txt");
            out.write(newId.getBytes());
            out.close();
            return newId;
        } catch (FileNotFoundException e) {
            File dir = new File("tweetImages");
            dir.mkdir();
            try {
                FileOutputStream out = new FileOutputStream("tweetImages/last_id.txt");
                out.write("1".getBytes());
                out.close();
                return "1";
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String downloadImage(String image){
        byte[] bytes = Base64.getDecoder().decode(image);
        String newLocation = "tweetImages/"+getNewId()+".jpg";
        try(FileOutputStream out = new FileOutputStream(newLocation)) {
            out.write(bytes);
        } catch (IOException e){
            e.printStackTrace();
        }
        return newLocation;
    }

    @Override
    public void handle(HttpExchange exchange){

        String requestUri = exchange.getRequestURI().toString();
        String[] segments = requestUri.split("/");
        String action = segments[2];
        String username = (String) exchange.getAttribute("username");

        log.info("New "+action+" request received for "+ username);


        boolean invalid = false;
        try {
            switch (action) {
                case "regular_tweet":
                    RequestTweet tweet = getTweet(exchange.getRequestBody());

                    String imgLocation = null;
                    if (!tweet.getImage().equals("")) {
                        imgLocation = downloadImage(tweet.getImage());
                    }
//                    manager.addTweet(username, tweet.getContent(), imgLocation);
                    break;
                case "quote":
                    int quoteId = Integer.parseInt(segments[3]);
                    RequestTweet quote = getTweet(exchange.getRequestBody());
                    manager.addQuote(quoteId, username, quote.getContent());
                    break;
                case "retweet":
                    int retweetId = Integer.parseInt(segments[3]);
                    manager.addRetweet(retweetId, username);
                    break;
                case "like":
                    int tweetToLikeId = Integer.parseInt(segments[3]);
                    if (exchange.getRequestMethod().equalsIgnoreCase("POST"))
                        manager.likeTweet(tweetToLikeId, username);
                    else if (exchange.getRequestMethod().equalsIgnoreCase("DELETE"))
                        manager.unlikeTweet(tweetToLikeId, username);
                    else
                        invalid = true;
                    break;

                case "comment":
                    int tweetToCommentId = Integer.parseInt(segments[3]);
                    RequestTweet comment = getTweet(exchange.getRequestBody());
                    manager.addReply(tweetToCommentId, username, comment.getContent());
                    break;
            }
        } catch (IOException e){
            invalid = true;
        }
        try {
            if (invalid) {
                exchange.sendResponseHeaders(400, 0);
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        exchange.close();
    }
}
