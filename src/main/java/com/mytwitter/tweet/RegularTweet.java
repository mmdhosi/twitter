package com.mytwitter.tweet;

import com.mytwitter.user.User;

public class RegularTweet extends Tweet{
    private final String type = "RegularTweet";
    private String image;


    public RegularTweet(String username, String content, int likeCount, int replyCount, int retweetCount) {
        super(username, content, likeCount, replyCount, retweetCount);
    }
    public String getType(){
        return type;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
