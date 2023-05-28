import java.sql.Timestamp;

public class Retweet extends Tweet{
    Tweet retweetedTweet;

    public Retweet(User user, int likeCount, int replyCount, int retweetCount, Tweet retweetedTweet) {
        super(user, "", likeCount, replyCount, retweetCount);
        this.retweetedTweet = retweetedTweet;
    }
}
