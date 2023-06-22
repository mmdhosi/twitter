import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public abstract class Tweet {
    //TODO:image
    private User user;
    private String content;
    private int likeCount = 0,replyCount = 0,retweetCount = 0;
    private Timestamp timestamp;
    private boolean liked;
    private int tweetId;


    public Tweet(User user, String content, int likeCount, int replyCount, int retweetCount) {
        this.user = user;
        this.content = content;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.retweetCount = retweetCount;
    }


    public void like(){
        liked = true;
    }

    public void unlike(){
        liked = false;
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
        return user.getUserName();
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
        return user.equals(tweet.user) && timestamp.equals(tweet.timestamp);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, timestamp);
    }

    @Override
    public String toString() {
        return user.getUserName() + ": \n"+ content +
                "\n  likes: "+likeCount+
                "  replies: "+replyCount+
                "  retweets: "+retweetCount+"\n";
    }
}
