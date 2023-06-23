package com.mytwitter.tweet;

import com.mytwitter.user.User;

import java.util.ArrayList;

public class Reply extends Tweet {

    Tweet repliedto;
    ArrayList<String> repliedToUsernames = new ArrayList<>();

    public Reply(User user, String content, int likeCount, int replyCount, int retweetCount, Tweet repliedto) {
        super(user, content, likeCount, replyCount, retweetCount);
        this.repliedto = repliedto;
    }
    //TODO: get replies from a tweet and its retweets
    public void addRepliedToUsername(String username){
        repliedToUsernames.add(username);
    }


}
