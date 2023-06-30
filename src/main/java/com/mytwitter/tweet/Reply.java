package com.mytwitter.tweet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Reply extends Tweet {
    private final String type = "Reply";
    private Tweet repliedTo;
    private Set<String> repliedToUsernames = new HashSet<>();

    public Reply(String username, String content, int likeCount, int replyCount, int retweetCount, Tweet repliedTo) {
        super(username, content, likeCount, replyCount, retweetCount);
        this.repliedTo = repliedTo;
    }
    //TODO: get replies from a tweet and its retweets
    public void addRepliedToUsername(String username){
        repliedToUsernames.add(username);
    }

    public Set<String> getRepliedToUsernames() {
        return repliedToUsernames;
    }

    public String getType() {
        return type;
    }

    public Tweet getRepliedTo() {
        return repliedTo;
    }
}
