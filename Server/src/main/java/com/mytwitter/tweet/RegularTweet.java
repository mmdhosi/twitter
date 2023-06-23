package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class RegularTweet extends Tweet{

    public RegularTweet(User user, String content, int likeCount, int replyCount, int retweetCount) {
        super(user, content, likeCount, replyCount, retweetCount);
    }
}
