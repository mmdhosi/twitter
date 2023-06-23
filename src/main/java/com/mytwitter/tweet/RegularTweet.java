package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class RegularTweet extends Tweet{
    private final String type = "RegularTweet";

    public RegularTweet(String username, String content, int likeCount, int replyCount, int retweetCount) {
        super(username, content, likeCount, replyCount, retweetCount);
    }
    public String getType(){
        return type;
    }
}
