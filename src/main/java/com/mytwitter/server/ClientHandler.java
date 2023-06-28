package com.mytwitter.server;

import com.mytwitter.server.database.Database;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.OutputType;
import com.mytwitter.tweet.Reply;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread{

    User currentUser;
    Database manager = Database.getManager();

    public ClientHandler(User currentUser) {
        this.currentUser = currentUser;

    }

    // TODO: upload avatar from client then call this method to add it to the users table
    private OutputType addAvatar(String location){
        return manager.addAvatar(location, currentUser.getUserName());
    }
    private OutputType addHeader(String location){
        return manager.addHeader(location, currentUser.getUserName());
    }
    private OutputType follow(String usernameToFollow){
        return manager.addFollower(usernameToFollow,currentUser.getUserName());
    }
    private OutputType unfollow(String usernameToUnfollow){
        return manager.removeFollower(usernameToUnfollow,currentUser.getUserName());
    }
    private ArrayList<UserProfile> userSearch(String wordToSearch){
        return manager.serverSearch(wordToSearch);
    }
    private ArrayList<User> getFollowers(){
        return manager.getFollowers(currentUser.getUserName());
    }
    private ArrayList<User> getFollowings(){
        return manager.getFollowings(currentUser.getUserName());
    }
    private OutputType addTweet(Tweet tweet){
        return manager.addTweet(tweet);
    }
    private OutputType addRetweet(int tweet){
        return manager.addRetweet(tweet, currentUser.getUserName());
    }
    private OutputType addQuote(int tweet, String quote){
        return manager.addQuote(tweet, currentUser.getUserName(), quote);
    }
    private OutputType addReply(int tweet, String reply){
        return manager.addReply(tweet, currentUser.getUserName(), reply);
    }
    private OutputType likeTweet(int tweet){
        return manager.likeTweet(tweet, currentUser.getUserName());
    }
    private OutputType unLikeTweet(int tweet){
        return manager.unlikeTweet(tweet, currentUser.getUserName());
    }
    private List<Tweet> getTimeline(){
        return manager.getTimeline(currentUser.getUserName());
    }
    private List<Reply> getReplies(int tweetId){
        return manager.getReplies(tweetId, currentUser.getUserName());
    }
    private OutputType block(String usernameToBlock){
        return manager.block(usernameToBlock,currentUser.getUserName());
    }
    private OutputType unblock(String usernameToUnblock){
        return manager.unblock(usernameToUnblock,currentUser.getUserName());
    }

    //TODO: show replies to a tweet recursively
    @Override
    public void run() {
//        follow("vex");
//        System.out.println(getFollowings());
//        Tweet tweet1 = manager.getTweet(12);
//        addTweet(tweet1);

//        addReply(tweet1,"ok shut up");
//        follow("mmd");
//        System.out.println(getReplies(manager.getTweet(1)));
//        Tweet tweet=new RegularTweet(currentUser,"oh my mm #boobs god #fuck",0,0,0);
//        System.out.println(getTimeline());
//        likeTweet(29);

//        addRetweet(28);
//        addQuote(28,"my brother for all the time #bro");
//        addReply(28,"yeah yeah #nice");

//        addTweet(tweet);
//        block("mmd");
//        follow("mmd");
//        follow("vex");
//        unblock("mmd");

//        block("mmd");

//        System.out.println(getTimeline());



    }

}
