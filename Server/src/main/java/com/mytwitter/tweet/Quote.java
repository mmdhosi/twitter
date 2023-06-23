package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class Quote extends Tweet{
    Tweet quotedTweet;

    public Quote(User user, String content, int likeCount, int replyCount, int retweetCount, Tweet quotedTweet) {
        super(user, content, likeCount, replyCount, retweetCount);
        this.quotedTweet = quotedTweet;
    }
}
