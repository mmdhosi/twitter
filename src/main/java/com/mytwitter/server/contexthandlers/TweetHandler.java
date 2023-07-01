package com.mytwitter.server.contexthandlers;

import com.mytwitter.poll.Poll;
import com.mytwitter.server.ServerGson;
import com.mytwitter.server.database.Database;
import com.mytwitter.tweet.*;
import com.mytwitter.util.ImageBase64;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;

public class TweetHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(TweetHandler.class);
    Database manager = Database.getManager();

    private RequestTweet getTweet(InputStream in) throws IOException {
        RequestTweet result = ServerGson.getGson().fromJson(new String(in.readAllBytes()), RequestTweet.class);
        in.close();
        return result;
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
                        imgLocation = ImageBase64.downloadImage("tweetImages/", tweet.getImage());
                    }
                    Poll poll = tweet.getPoll();
                    int pollId = 0;
                    if (poll != null) {
                        pollId = manager.addPoll(poll);
                    }

                    manager.addTweet(username, tweet.getContent(), imgLocation, pollId);
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
