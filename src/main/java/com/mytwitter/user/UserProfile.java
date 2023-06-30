package com.mytwitter.user;

import com.mytwitter.tweet.Tweet;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class UserProfile implements Serializable {
    User user;
    int countFollowers,countFollowings;
    Boolean isFollowed,isBlocked;
    String header,avatar;
    ArrayList<Tweet> tweets;
    ArrayList<UserProfile> followers;
    ArrayList<UserProfile> followings;

    public void setFollowers(ArrayList<UserProfile> followers) {
        this.followers = followers;
    }

    public void setFollowings(ArrayList<UserProfile> followings) {
        this.followings = followings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getFollowed() {
        return isFollowed;
    }

    public void setFollowed(Boolean followed) {
        isFollowed = followed;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public int getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(int countFollowers) {
        this.countFollowers = countFollowers;
    }

    public int getCountFollowings() {
        return countFollowings;
    }

    public void setCountFollowings(int countFollowings) {
        this.countFollowings = countFollowings;
    }

    public String getHeader() {
        return header;
    }
    private String convertImageToString(Blob blob){
        try {
            byte[] imageInBytes = blob.getBinaryStream().readAllBytes();
            return Base64.getEncoder().encodeToString(imageInBytes);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bio getBio(){
        return bio;
    }

    public void setHeader(Blob header) {
        if(header != null)
            this.header = convertImageToString(header);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        if(avatar != null)
            this.avatar = convertImageToString(avatar);
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "user=" + user +
                ", countFollowers=" + countFollowers +
                ", countFollowings=" + countFollowings +
                ", tweets=" + tweets +
                '}';
    }
}
