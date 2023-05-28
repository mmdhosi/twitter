import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread{

    User currentUser;
    DatabaseManager manager = DatabaseManager.getManager();

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
    private ArrayList<User> userSearch(String wordToSearch){
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
    private OutputType addRetweet(Tweet tweet){
        return manager.addRetweet(tweet, currentUser.getUserName());
    }
    private OutputType addQuote(Tweet tweet, String quote){
        return manager.addQuote(tweet, currentUser.getUserName(), quote);
    }
    private OutputType addReply(Tweet tweet, String reply){
        return manager.addReply(tweet, currentUser.getUserName(), reply);
    }
    private OutputType likeTweet(Tweet tweet){
        return manager.likeTweet(tweet, currentUser.getUserName());
    }
    private OutputType unLikeTweet(Tweet tweet){
        return manager.unlikeTweet(tweet, currentUser.getUserName());
    }
    private List<Tweet> getTimeline(){
        return manager.getTimeline(currentUser.getUserName());

    }
    //TODO: show replies to a tweet recursively
    @Override
    public void run() {
//        follow("vex");
//        System.out.println(getFollowings());
//        Tweet tweet1 = manager.getTweet(10);
//        addTweet(tweet1);

//        addReply(tweet1,"ok shut up");
//        follow("mmd");
        System.out.println(getTimeline());



    }
}
