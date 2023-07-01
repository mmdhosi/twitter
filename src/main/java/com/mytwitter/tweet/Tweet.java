package com.mytwitter.tweet;

import java.sql.Timestamp;
import java.util.Objects;

public abstract class Tweet {
    private String username;
    private String content;
    private int likeCount = 0,replyCount = 0,retweetCount = 0;
    private Timestamp timestamp;
    private boolean liked;
    private int tweetId;


    public Tweet(String username, String content, int likeCount, int replyCount, int retweetCount) {
        this.username = username;
        this.content = content;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.retweetCount = retweetCount;
    }




    public void setLiked(){
        liked = true;
    }

    public void setUnliked(){
        liked = false;
    }

    public void addLike(){
        likeCount++;
    }
    public void removeLike(){
        likeCount--;
    }

    public boolean isLiked(){
        return liked;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public String getUserName() {
        return username;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return username.equals(tweet.username) && timestamp.equals(tweet.timestamp);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, timestamp);
    }

    @Override
    public String toString() {
        return username + ": \n"+ content +
                "\n  likes: "+likeCount+
                "  replies: "+replyCount+
                "  retweets: "+retweetCount+"\n"+
                liked;
    }


}
