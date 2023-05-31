import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseManager {
    private static Connection con;
    private static DatabaseManager manager;
    private final static int FAVE_STAR_LIKE_COUNT = 10;

    static{
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter", "root", "HoMo1727");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DatabaseManager() {
    }

    public static DatabaseManager getManager(){
        if(manager == null)
            manager = new DatabaseManager();
        return manager;
    }

    public OutputType addUser(User user){
        Timestamp registerDate = Timestamp.valueOf(LocalDateTime.now());
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.users VALUES(DEFAULT ,?,?,?,?,?,?,?,?,?,DEFAULT,DEFAULT,DEFAULT,DEFAULT)");
            statement.setString(1,user.getUserName());
            statement.setString(2,user.getFirstName());
            statement.setString(3,user.getLastName());
            statement.setString(4,user.getEmail());
            statement.setString(5,user.getPhoneNumber());
            statement.setString(6,user.getPassword());
            statement.setString(7,user.getCountry());
            statement.setString(8,user.getBirthdate());
            statement.setTimestamp(9,registerDate);
            statement.executeUpdate();

        } catch (SQLException e) {
            if(e.getErrorCode() == 1062){
                String message = e.getMessage();
                System.out.println(message);
                if(message.contains("'users.user_name'"))
                    return OutputType.DUPLICATE_USERNAME;
                else if(message.contains("'users.email_UNIQUE'"))
                    return OutputType.DUPLICATE_EMAIL;
                else if(message.contains("'users.phoneNumber_UNIQUE'"))
                    return OutputType.DUPLICATE_PHONENUMBER;
            }
        }
        return OutputType.SUCCESS;
    }

    public User getUser(String userName){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.users WHERE user_name=?");
            statement.setString(1, userName);
            ResultSet result = statement.executeQuery();
            result.next();

            return new User(
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private User getUserFromId(int id){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.users WHERE id=?");
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            result.next();

            return new User(
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: add feature for editing bio record

    public int createBioRecord(String username, Bio bio){
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.bio VALUES(DEFAULT ,?,?,?,?)");
            statement.setString(1, username);
            statement.setString(2, bio.getLocation());
            statement.setString(3, bio.getWebAddress());
            statement.setString(4, bio.getText());
            statement.executeUpdate();

            statement = con.prepareStatement("SELECT id FROM twitter.bio WHERE username = ?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            result.next();
            return Integer.parseInt(result.getString(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public OutputType addBio(String username, Bio bio){
        //TODO: check bio length in client
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET bio_id= ? WHERE user_name=?");
            statement.setInt(1, createBioRecord(username, bio));
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }

    public OutputType addAvatar(String location, String username) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET avatar= ? WHERE user_name=?");
            statement.setString(1, location);
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }
    public OutputType addHeader(String location, String username) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET header= ? WHERE user_name=?");
            statement.setString(1, location);
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }

    private int getUserId(String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.users WHERE user_name = ?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public OutputType addFollower(String following, String follower){
        try {
            int followerId=getUserId(follower);
            int followingId=getUserId(following);
            if(followingId==-1 || followerId==-1){
                return OutputType.NOT_FOUND;
            }
            if(followingId == followerId){
                return OutputType.INVALID;
            }

            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.followers VALUES(?,?)");
            statement.setInt(1, followingId);
            statement.setInt(2,followerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;
    }
    public OutputType removeFollower(String following, String follower){
        try {
            int followerId=getUserId(follower);
            int followingId=getUserId(following);
            if(followingId==-1 || followerId==-1){
                return OutputType.NOT_FOUND;
            }
            if(followingId == followerId){
                return OutputType.INVALID;
            }

            PreparedStatement statement = con.prepareStatement("DELETE FROM twitter.followers WHERE follower_id= ? AND following_id = ?");
            statement.setInt(1,followerId);
            statement.setInt(2, followingId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;
    }
    public ArrayList<User> serverSearch(String wordToSearch)  {
        ArrayList<User> users=new ArrayList<>();
        wordToSearch="%"+wordToSearch+"%";
        try {
            PreparedStatement statement = con.prepareStatement("SELECT user_name FROM twitter.users WHERE  first_name LIKE ? or last_name LIKE ? or user_name LIKE ?");
            statement.setString(1,wordToSearch);
            statement.setString(2, wordToSearch);
            statement.setString(3, wordToSearch);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                users.add(getUser(result.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }
    public ArrayList<User> getFollowers(String user_id){
        ArrayList<User> users=new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT user_name\n" +
                    "FROM twitter.followers \n" +
                    "JOIN users ON follower_id = id\n" +
                    "WHERE  following_id = ? ");

            statement.setInt(1,getUserId(user_id));
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                users.add(getUser(result.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public ArrayList<User> getFollowings(String user_id){
        ArrayList<User> users=new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT user_name\n" +
                    "FROM twitter.followers \n" +
                    "JOIN users ON following_id = id\n" +
                    "WHERE  follower_id = ? ");

            statement.setInt(1,getUserId(user_id));
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                users.add(getUser(result.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    //TODO:Check
    public Tweet getTweet(int tweetId){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.tweets WHERE id=?");
            statement.setInt(1, tweetId);
            ResultSet result = statement.executeQuery();
            result.next();
            Tweet resultTweet = null;
            if(Objects.equals(result.getString(3), "Q"))
                resultTweet = new Quote(getUserFromId(result.getInt(2)),
                    result.getString(7),
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    getTweet(result.getInt(10)));
            else if(result.getString(3).equals("R"))
                resultTweet = new Retweet(getUserFromId(result.getInt(2)),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        getTweet(result.getInt(8)));
            else if(result.getString(3).equals("T"))
                resultTweet = new RegularTweet(getUserFromId(result.getInt(2)),
                        result.getString(7),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6));
            else if(result.getString(3).equals("P"))
                resultTweet = new Reply(getUserFromId(result.getInt(2)),
                        result.getString(7),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        getTweet(result.getInt(12)));
            if(resultTweet == null)
                return null;
            resultTweet.setTweetId(result.getInt(1));
            resultTweet.setTimestamp(result.getTimestamp(11));
            return resultTweet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashSet<Tweet> getFaveStars(){
        HashSet<Tweet> timeline = new HashSet<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE like_count>=?");
            statement.setInt(1, FAVE_STAR_LIKE_COUNT);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                timeline.add(getTweet(result.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeline;
    }



    public List<Tweet> getTimeline(String userName){
        //TODO: if it is retweeted to it self???
        ArrayList<User> followings=getFollowings(userName);
        HashSet<Tweet> timeline = new HashSet<>();
        for (User f:followings) {
            try {
                PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE user_id=?");
                statement.setInt(1, getUserId(f.getUserName()));
                ResultSet result = statement.executeQuery();
                while(result.next()){
                    timeline.add(getTweet(result.getInt(1)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        timeline.addAll(getFaveStars());
        List<Tweet> tweets=new ArrayList<>(timeline);
        Collections.sort(tweets, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
        return tweets;
    }

    public int idTweetByTimeAndUserid(String userName, Timestamp timestamp){
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE time = ? AND user_id = ?");
            timestamp.setNanos(0);
            statement.setTimestamp(1, timestamp);
            statement.setInt(2, getUserId(userName));
            ResultSet result = statement.executeQuery();
            result.next();
            int tweetId = result.getInt(1);
            return tweetId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void extractHashtags(String content, int tweetId){
        ArrayList<String> hashtags = new ArrayList<>();
        PreparedStatement statement;
        ResultSet result;
        int hashtag_id;
        for (String word : content.split(" ")) {
            if(word.charAt(0) == '#'){
                hashtags.add(word);
                try {
                    statement = con.prepareStatement("SELECT hashtag_id FROM hashtag_names WHERE hashtag_name = ?");
                    statement.setString(1, word);
                    result = statement.executeQuery();
                    if(!result.next()) {
                        statement = con.prepareStatement("INSERT INTO hashtag_names VALUES(DEFAULT, ?)");
                        statement.setString(1, word);
                        statement.executeUpdate();
                        statement = con.prepareStatement("SELECT last_insert_id() FROM hashtag_names");
                        result = statement.executeQuery();
                        result.next();
                        hashtag_id = result.getInt(1);
                    } else {
                        hashtag_id = result.getInt(1);
                    }
                    statement = con.prepareStatement("INSERT INTO tweet_hashtags VALUES(?,?)");
                    statement.setInt(1, tweetId);
                    statement.setInt(2, hashtag_id);
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public OutputType addTweet(Tweet tweet){
        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
            //TODO: handle image
            //TODO:280 char limit
            //TODO: extract hashtag and add it to its table  DONE
            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,?,NULL,NULL,NULL,?, NULL)");

            statement.setInt(1, getUserId(tweet.getUserName()));
            statement.setString(2, "T");
            statement.setInt(3, tweet.getLikeCount());
            statement.setInt(4, tweet.getReplyCount());
            statement.setInt(5, tweet.getRetweetCount());
            statement.setString(6, tweet.getContent());
            statement.setTimestamp(7, tweetDate);
            tweet.setTimestamp(tweetDate);
            int newTweetId;
            synchronized (this) {
                statement.executeUpdate();
                statement = con.prepareStatement("SELECT last_insert_id() FROM tweets");
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                newTweetId = resultSet.getInt(1);
            }
            extractHashtags(tweet.getContent(), newTweetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;

    }
    public OutputType addRetweet(int retweetedId, String userName){
        //TODO: add count retweet_count  DONE
        //TODO: handle if a retweet gets retweeted  DONE

        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
            //TODO: handle image
            //TODO:280 char limit

            statement = con.prepareStatement("SELECT tweet_type FROM twitter.tweets WHERE id=?");
            statement.setInt(1, retweetedId);
            ResultSet result = statement.executeQuery();
            result.next();
            if(Objects.equals(result.getString(1), "R")){
                statement = con.prepareStatement("SELECT retweeted_id FROM twitter.tweets WHERE id=?");
                statement.setInt(1, retweetedId);
                result = statement.executeQuery();
                result.next();
                return addRetweet(result.getInt(1),userName);
            }

            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,NULL,?,NULL,NULL,?, NULL)");
            statement.setInt(1, getUserId(userName));
            statement.setString(2, "R");
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setInt(5, 0);
            statement.setInt(6, retweetedId);
            statement.setTimestamp(7, tweetDate);
            statement.executeUpdate();
            statement = con.prepareStatement("UPDATE twitter.tweets SET retweet_count = retweet_count+1 WHERE id = ?");
            statement.setInt(1, retweetedId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;

    }

    public OutputType addQuote(int tweetToQuoteId, String userName, String quote){
        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
            //TODO: handle image
            //TODO: 280 char limit
            //TODO: extract hashtag and add it to its table DONE
            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,?,NULL,NULL,?,?,NULL)");
            statement.setInt(1, getUserId(userName));
            statement.setString(2, "Q");
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setInt(5, 0);
            statement.setString(6, quote);
            statement.setInt(7, tweetToQuoteId);
            statement.setTimestamp(8, tweetDate);
            int newTweetId;
            synchronized (this) {
                statement.executeUpdate();
                statement = con.prepareStatement("SELECT last_insert_id() FROM tweets");
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                newTweetId = resultSet.getInt(1);
            }
            extractHashtags(quote,newTweetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;

    }
    public OutputType addReply(int tweetToReplyId, String userName, String reply){
        //TODO: add count reply_count  DONE

        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
            //TODO: handle image
            //TODO: 280 char limit
            //TODO: extract hashtag and add it to its table

            statement = con.prepareStatement("SELECT tweet_type FROM twitter.tweets WHERE id=?");
            statement.setInt(1, tweetToReplyId);
            ResultSet result = statement.executeQuery();
            result.next();
            if(Objects.equals(result.getString(1), "R")){
                statement = con.prepareStatement("SELECT retweeted_id FROM twitter.tweets WHERE id=?");
                statement.setInt(1, tweetToReplyId);
                ResultSet result1 = statement.executeQuery();
                result1.next();
                return addReply(result1.getInt(1),userName,reply);
            }


            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,?,NULL,NULL,NULL,?,?)");
            statement.setInt(1, getUserId(userName));
            statement.setString(2, "P");
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setInt(5, 0);
            statement.setString(6, reply);
            statement.setTimestamp(7, tweetDate);
            statement.setInt(8, tweetToReplyId);
            statement.executeUpdate();

            statement = con.prepareStatement("UPDATE twitter.tweets SET reply_count = reply_count+1 WHERE id = ?");
            statement.setInt(1, tweetToReplyId);
            int newTweetId;
            synchronized (this) {
                statement.executeUpdate();
                statement = con.prepareStatement("SELECT last_insert_id() FROM tweets");
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                newTweetId = resultSet.getInt(1);
            }
            extractHashtags(reply,newTweetId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;

    }

    public ArrayList<Retweet> getRetweets(int tweetId){
        ArrayList<Retweet> retweets = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("""
                        SELECT id
                        FROM twitter.tweets\s
                        WHERE  retweeted_id = ?\s""");

            statement.setInt(1, tweetId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Retweet retweet = (Retweet) getTweet(result.getInt(1));
                retweet.setTweetId(result.getInt(1));
                retweets.add(retweet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retweets;
    }

    public ArrayList<Reply> getReplies(int tweetToShowId, String username){
        ArrayList<Reply> replies = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("""
                    SELECT id
                    FROM twitter.tweets\s
                    WHERE  replied_to = ?\s""");
            statement.setInt(1, tweetToShowId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                // get all replies to this tweet
                int reply_id = result.getInt(1);
                Reply reply = (Reply) getTweet(reply_id);
                reply.addRepliedToUsername(username);
                replies.add(reply);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        // get all replies to this tweet's retweets
        for (Retweet retweet:getRetweets(tweetToShowId)){
            for (Reply reply : getReplies(retweet.getTweetId(), retweet.getUserName())) {
                reply.addRepliedToUsername(retweet.getUserName());
                replies.add(reply);
            }
        }

        return replies;
    }

    public OutputType likeTweet(int tweetToLikeId, String userName){
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("SELECT tweet_type FROM twitter.tweets WHERE id=?");
            statement.setInt(1, tweetToLikeId);
            ResultSet result = statement.executeQuery();
            result.next();
            if(Objects.equals(result.getString(1), "R")){
                statement = con.prepareStatement("SELECT retweeted_id FROM twitter.tweets WHERE id=?");
                statement.setInt(1, tweetToLikeId);
                result = statement.executeQuery();
                result.next();
                return likeTweet(result.getInt(1),userName);
            }

            statement = con.prepareStatement("INSERT INTO twitter.likes VALUES(?,?)");

            statement.setInt(1, tweetToLikeId);
            statement.setInt(2, getUserId(userName));
            statement.executeUpdate();


            statement = con.prepareStatement("UPDATE twitter.tweets SET like_count = like_count+1 WHERE id = ?");
            statement.setInt(1, tweetToLikeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;
    }

    public OutputType unlikeTweet(int tweetToUnlikeId, String userName){
        //TODO: handle if its retweet    DONE

        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("SELECT tweet_type FROM twitter.tweets WHERE id=?");
            statement.setInt(1, tweetToUnlikeId);
            ResultSet result = statement.executeQuery();
            result.next();
            if(Objects.equals(result.getString(1), "R")){
                statement = con.prepareStatement("SELECT retweeted_id FROM twitter.tweets WHERE id=?");
                statement.setInt(1, tweetToUnlikeId);
                result = statement.executeQuery();
                result.next();
                return unlikeTweet(result.getInt(1),userName);
            }



            statement = con.prepareStatement("DELETE FROM twitter.likes WHERE user_id = ? AND tweet_id  = ?");
            statement.setInt(1, getUserId(userName));
            statement.setInt(2, tweetToUnlikeId);
            statement.executeUpdate();

            statement = con.prepareStatement("UPDATE twitter.tweets SET like_count = like_count-1 WHERE id = ?");
            statement.setInt(1, tweetToUnlikeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;
    }

}
