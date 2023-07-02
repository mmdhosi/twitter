package com.mytwitter.server.database;

import com.mytwitter.poll.Answer;
import com.mytwitter.poll.Poll;
import com.mytwitter.server.Config;
import com.mytwitter.tweet.*;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.mytwitter.direct.Direct;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.direct.Message;
import com.mytwitter.util.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Database {
    private static Connection con;
    private static Database manager;
    private final static int FAVE_STAR_LIKE_COUNT = 10;

    private static final Logger log = LoggerFactory.getLogger(Database.class);


    static{
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter", Config.getSQLUser(), Config.getSQLPass());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Database() {}

    public static Database getManager(){
        if(manager == null)
            manager = new Database();
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
                log.info(message);
                if(message.contains("'users.user_name'"))
                    return OutputType.DUPLICATE_USERNAME;
                else if(message.contains("'users.email_UNIQUE'"))
                    return OutputType.DUPLICATE_EMAIL;
                else if(message.contains("'users.phoneNumber_UNIQUE'"))
                    return OutputType.DUPLICATE_PHONENUMBER;
                else
                    return OutputType.INVALID;
            }
        }
        return OutputType.SUCCESS;
    }

    public User getUser(String userName){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.users WHERE username=?");
            statement.setString(1, userName);
            ResultSet result = statement.executeQuery();
            result.next();


            if(!result.getString(2).equals(userName)){
                return null;
            }
            User user = new User(
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9));
            user.setJoinDate(result.getString(10));
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public OutputType addMessage(String sender,String receiver,String content){
        Timestamp registerDate = Timestamp.valueOf(LocalDateTime.now());
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.direct VALUES(DEFAULT ,?,?,?,?,?)");
            statement.setInt(1,getUserId(sender));
            statement.setInt(2,getUserId(receiver));
            statement.setString(3,content);
            statement.setTimestamp(4,registerDate);
            statement.setBoolean(5,false);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return OutputType.INVALID;
        }
        return OutputType.SUCCESS;
    }

    public OutputType setSeen(int idMessage){
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.direct SET seen= ? WHERE id=?");
            statement.setBoolean(1,true );
            statement.setInt(2, idMessage);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }

    public ArrayList<Message> getAllMessage(String username1,String username2){
        ArrayList<Message> messages=new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.direct WHERE sender=? and receiver=? or sender=? and receiver=?");
            statement.setInt(1, getUserId(username1));
            statement.setInt(2, getUserId(username2));

            statement.setInt(3, getUserId(username2));
            statement.setInt(4, getUserId(username1));

            ResultSet result = statement.executeQuery();
            while (result.next()){
                Message message=new Message(
                        result.getInt(1),
                        Objects.requireNonNull(getUserFromId(result.getInt(2))).getUserName(),
                        Objects.requireNonNull(getUserFromId(result.getInt(3))).getUserName(),
                        result.getString(4),
                        result.getTimestamp(5),
                        result.getBoolean(6)
                );
                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        messages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getSendTime().compareTo(o2.getSendTime());
            }
        });
        return messages;
    }
    public ArrayList<Direct> getDirect(String requesterUsername){
        ArrayList<Direct> directs=new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT DISTINCT sender FROM twitter.direct WHERE receiver=?");
            statement.setInt(1, getUserId(requesterUsername));
            ResultSet result = statement.executeQuery();
            while (result.next()){
                String sender= Objects.requireNonNull(getUserFromId(result.getInt(1))).getUserName();
                ArrayList<Message> messages = getAllMessage(requesterUsername,sender);

                Direct direct=new Direct(sender, messages.get(messages.size()-1));
                directs.add(direct);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return directs;
    }

    public int addPoll(Poll poll){
        int questionId = 0;


        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.poll_questions VALUES(DEFAULT ,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,poll.getQuestion());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                questionId = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        for (Answer a:poll.getAnswers()) {
            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.poll_answers VALUES(DEFAULT ,?,?,?)");
                statement.setInt(1,questionId);
                statement.setString(2,a.getAnswer());
                statement.setInt(3,0);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return questionId;
    }
    public Poll getPoll(int id){
        Poll poll=new Poll();
        try {
            PreparedStatement statement = con.prepareStatement("select question from twitter.poll_questions where id=?");
            statement.setInt(1,id);
            ResultSet result = statement.executeQuery();
            result.next();
            poll.setQuestion(result.getString(1));
            poll.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement statement = con.prepareStatement("select * from twitter.poll_answers where question_id=?");
            statement.setInt(1,id);
            ResultSet result = statement.executeQuery();
            while (result.next()){
                Answer answer=new Answer();
                answer.setId(result.getInt(1));
                answer.setAnswer(result.getString(3));
                answer.setVotes(result.getInt(4));
                poll.addAnswer(answer);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return poll;
    }
    public int getPollAnswerByUser(int id,String username){

      try {
        PreparedStatement statement = con.prepareStatement("select id from twitter.poll_answers where question_id=?");
        statement.setInt(1,id);
        ResultSet result = statement.executeQuery();
        // list of answers to question id
        while (result.next()){
            statement = con.prepareStatement("select user_id from twitter.poll_user_answers where poll_answer_id=?");
            statement.setInt(1,result.getInt(1));
            ResultSet result_user = statement.executeQuery();
            // list of users
            while (result_user.next()){
                if(Objects.equals(Objects.requireNonNull(getUserFromId(result_user.getInt(1))).getUserName(), username)){
                    return result.getInt(1);
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return -2;
    }
        return -1;
    }
    public OutputType setPollAnswer(int id,String username){

        try {
            PreparedStatement statement = con.prepareStatement("insert into  twitter.poll_user_answers VALUES(DEFAULT ,?,?) ");
            statement.setInt(1,getUserId(username));
            statement.setInt(2,id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return OutputType.INVALID;
        }
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE poll_answers SET votes = votes + 1 WHERE id = ? ");
            statement.setInt(1,id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return OutputType.INVALID;
        }

        return OutputType.SUCCESS;

    }




    public Boolean checkFollowed(String usernameToView,String usernameToRequest){
        ArrayList<User> followings=getFollowings(usernameToRequest);
        User user=getUser(usernameToView);
        for (User f:followings) {
            if(Objects.equals(f.getUserName(), user.getUserName())){
                return true;
            }
        }

        return false;
    }
    public Boolean checkBlocked(String usernameToView,String usernameToRequest){
        ArrayList<User> blocked=getBlocklist(usernameToRequest);
        User user=getUser(usernameToView);
        for (User f:blocked) {
            if(Objects.equals(f.getUserName(), user.getUserName())){
                return true;
            }
        }

        return false;
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

    private int createBioRecord(String username, Bio bio){
        try {
            // add a new bio record
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.bio VALUES(DEFAULT ,?,?,?,?)");
            statement.setString(1, username);
            statement.setString(2, bio.getLocation());
            statement.setString(3, bio.getWebAddress());
            statement.setString(4, bio.getText());
            statement.executeUpdate();

            // get the new bio id to connect it to user in users table
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
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET bio_id= ? WHERE username=?");
            statement.setInt(1, createBioRecord(username, bio));
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }

    public Bio getBio(String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT text, location, web_adress FROM twitter.bio WHERE username = ?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            result.next();
            return new Bio(result.getString(1), result.getString(2), result.getString(3));

        } catch (SQLException e) {
            log.info("no bio found for user: "+ username);
            return new Bio();
        }
    }

    public OutputType addAvatar(String location, String username) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET avatar= ? WHERE username=?");
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
            PreparedStatement statement = con.prepareStatement("UPDATE twitter.users SET header= ? WHERE username=?");
            statement.setString(1, location);
            statement.setString(2, username);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return OutputType.SUCCESS;
    }
    public OutputType editProfile(UserProfile userProfile){
        if(userProfile.getHeader()!=null){
            String headerLocation = ImageBase64.downloadImage("avatars", userProfile.getHeader());
            addHeader(headerLocation,userProfile.getUser().getUserName());
        }
        if(userProfile.getAvatar()!=null){
            String avatarLocation = ImageBase64.downloadImage("avatars", userProfile.getAvatar());
            addAvatar(avatarLocation,userProfile.getUser().getUserName());
        }
        if ((userProfile.getBio()!=null)){
            editBio(userProfile.getUser().getUserName(),userProfile.getBio());
        }
        return OutputType.SUCCESS;
    }

    private void editBio(String userName, Bio bio) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.bio WHERE username=?");
            statement.setString(1, userName);
            ResultSet result = statement.executeQuery();
            if(!result.next()){
                addBio(userName, bio);
            } else {
                statement = con.prepareStatement("UPDATE twitter.bio SET location = ?, web_adress = ?, text = ? WHERE username = ?");
                statement.setString(1, bio.getLocation());
                statement.setString(2, bio.getWebAddress());
                statement.setString(3, bio.getText());
                statement.setString(4, userName);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private int getUserId(String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.users WHERE username = ?");
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
            int followerId = getUserId(follower);
            int followingId = getUserId(following);
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
    public OutputType block(String userToBlock, String user){
        try {
            int userIdToBlock=getUserId(userToBlock);
            int userId=getUserId(user);
            if(userIdToBlock==-1 || userId==-1){
                return OutputType.NOT_FOUND;
            }
            if(userId == userIdToBlock){
                return OutputType.INVALID;
            }
            PreparedStatement statement = con.prepareStatement("INSERT INTO twitter.blocklist VALUES(?,?)");
            statement.setInt(1, userId);
            statement.setInt(2,userIdToBlock);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        removeFollower(userToBlock,user);
        removeFollower(user,userToBlock);
        return OutputType.SUCCESS;
    }
    public ArrayList<User> getBlocklist(String user_id){
        ArrayList<User> users=new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT username\n" +
                    "FROM twitter.blocklist \n" +
                    "JOIN users ON user_to_block_id = id\n" +
                    "WHERE  user_id = ? ");

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
    public OutputType unblock(String userToUnblock, String user){
        try {
            int userIdToUnblock=getUserId(userToUnblock);
            int userId=getUserId(user);
            if(userIdToUnblock==-1 || userId==-1){
                return OutputType.NOT_FOUND;
            }
            if(userId == userIdToUnblock){
                return OutputType.INVALID;
            }
            if(userId == userIdToUnblock){
                return OutputType.INVALID;
            }

            PreparedStatement statement = con.prepareStatement("DELETE FROM twitter.blocklist WHERE user_id= ? AND user_to_block_id = ?");
            statement.setInt(1,userId);
            statement.setInt(2, userIdToUnblock);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;
    }

    public ArrayList<UserProfile> serverSearch(String wordToSearch)  {

        ArrayList<UserProfile> users=new ArrayList<>();
        wordToSearch="%"+wordToSearch+"%";
        try {
            PreparedStatement statement = con.prepareStatement("SELECT username FROM twitter.users WHERE  first_name LIKE ? or last_name LIKE ? or username LIKE ?");
            statement.setString(1,wordToSearch);
            statement.setString(2, wordToSearch);
            statement.setString(3, wordToSearch);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String username = result.getString(1);
                User user = getUser(username);
                UserProfile profile =new UserProfile();
                profile.setUser(user);
                profile.setAvatar(getAvatar(username));
                users.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;

    }
    public String getAvatar(String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT avatar\n" +
                    "FROM twitter.users WHERE username= ? ");

            statement.setString(1,username);
            ResultSet result = statement.executeQuery();
            result.next();
            String imgLocation = result.getString(1);
            return ImageBase64.getImageFromLocation(imgLocation);

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getHeader(String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT header\n" +
                    "FROM twitter.users WHERE username= ? ");

            statement.setString(1,username);
            ResultSet result = statement.executeQuery();
            result.next();
            String imgLocation = result.getString(1);
            return ImageBase64.getImageFromLocation(imgLocation);
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<User> getFollowers(String user_id){
        ArrayList<User> users=new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT username\n" +
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
            PreparedStatement statement = con.prepareStatement("SELECT username\n" +
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
    public Tweet getTweet(int tweetId, String username){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM twitter.tweets WHERE id=?");
            statement.setInt(1, tweetId);
            ResultSet result = statement.executeQuery();
            result.next();
            Tweet resultTweet = null;
            if(Objects.equals(result.getString(3), "Q"))
                resultTweet = new Quote(getUserFromId(result.getInt(2)).getUserName(),
                    result.getString(7),
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    getTweet(result.getInt(10), username));
            else if(result.getString(3).equals("R"))
                resultTweet = new Retweet(getUserFromId(result.getInt(2)).getUserName(),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        getTweet(result.getInt(8), username));
            else if(result.getString(3).equals("T")) {
                RegularTweet tweet = new RegularTweet(getUserFromId(result.getInt(2)).getUserName(),
                        result.getString(7),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6));
                String imgLocation = result.getString(9);
                if(imgLocation != null){
                    try {
                        tweet.setImage(ImageBase64.convertToBase64(new FileInputStream(imgLocation)));
                    } catch (FileNotFoundException e) {
                        tweet.setImage(null);
                    }
                }
                if(result.getInt(13) != 0){
                    tweet.setPoll(getPoll(result.getInt(13)));
                }

                resultTweet = tweet;
            }
            else if(result.getString(3).equals("P")) {
                Tweet repliedToTweet = getTweet(result.getInt(12), username);
                Reply reply = new Reply(getUserFromId(result.getInt(2)).getUserName(),
                        result.getString(7),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        repliedToTweet);
                //FIXME: this is temporary. make function that returns all the usernames that this comment is replying to
                reply.addRepliedToUsername(repliedToTweet.getUserName());
                resultTweet = reply;
            }
            if(resultTweet == null)
                return null;
            resultTweet.setTweetId(result.getInt(1));
            resultTweet.setTimestamp(result.getTimestamp(11));


            if(checkIfLiked(resultTweet.getTweetId(), username))
                resultTweet.setLiked();
            if(resultTweet instanceof RegularTweet regularTweet){
                Poll poll = regularTweet.getPoll();
                if(poll != null)
                    poll.setAnsweredId(getPollAnswerByUser(poll.getId(), username));
            }


            return resultTweet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashSet<Tweet> getFaveStars(String userName){
        HashSet<Tweet> timeline = new HashSet<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE like_count>=?");
            statement.setInt(1, FAVE_STAR_LIKE_COUNT);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                Tweet tweet=getTweet(result.getInt(1), userName);
                timeline.add(tweet);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeline;
    }



    public List<Tweet> getTimeline(String userName){
        if(getUserId(userName) == -1){
            return null;
        }

        ArrayList<User> followings=getFollowings(userName);
        HashSet<Tweet> timeline = new HashSet<>();
        for (User following:followings) {
            try {
                PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE user_id=?");
                statement.setInt(1, getUserId(following.getUserName()));
                ResultSet result = statement.executeQuery();
                Tweet tweet;
                while(result.next()){
                    tweet = getTweet(result.getInt(1), userName);
                    timeline.add(tweet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        timeline.addAll(getFaveStars(userName));
        List<Tweet> tweets=new ArrayList<>(timeline);
        Collections.sort(tweets, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        ArrayList<User> blocklist=getBlocklist(userName);
        for (User b:blocklist) {
            tweets.removeIf(t -> Objects.equals(b.getUserName(), t.getUserName()));
        }
        return tweets;
    }

    public List<Tweet> getTweetsForUser(String userName, String requesterUsername){
        ArrayList<User> followings=getFollowings(userName);
        HashSet<Tweet> tweetSet = new HashSet<>();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM twitter.tweets WHERE user_id=?");
            statement.setInt(1, getUserId(userName));
            ResultSet result = statement.executeQuery();
            while(result.next()){
                tweetSet.add(getTweet(result.getInt(1), requesterUsername));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Tweet> tweets=new ArrayList<>(tweetSet);
        Collections.sort(tweets, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet o1, Tweet o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        return tweets;
    }

    public void extractHashtags(String content, int tweetId){
        if(content == null || content.equals(""))
            return;
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

    public ArrayList<Tweet> findTweetsByHashtags(String hashtag, String username){
        if (!hashtag.contains("#") ) {
            hashtag="#"+hashtag;
        }
        ArrayList<Tweet> tweets=new ArrayList<>();
        PreparedStatement statement;
        ResultSet result;
        try {
            statement = con.prepareStatement("SELECT hashtag_id FROM hashtag_names WHERE hashtag_name = ?");
            statement.setString(1, hashtag);
            result = statement.executeQuery();
            result.next();
            statement = con.prepareStatement("SELECT tweet_id FROM tweet_hashtags WHERE hashtag_id = ?");
            statement.setInt(1, result.getInt(1));
            result = statement.executeQuery();
            while (result.next()){
                tweets.add(getTweet(result.getInt(1), username));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;


    }

    public OutputType addTweet(String username, String content, String imgLocation, int pollId){
        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement("INSERT INTO twitter.tweets(user_id,tweet_type,content, image_location, time, poll_id) VALUES(?,?,?,?,?,?)");

            statement.setInt(1, getUserId(username));
            statement.setString(2, "T");
            statement.setString(3, content);
            statement.setTimestamp(5, tweetDate);
            if(imgLocation != null && !imgLocation.equals(""))
                statement.setString(4, imgLocation);
            else
                statement.setNull(4, Types.VARCHAR);
            if(pollId != 0)
                statement.setInt(6, pollId);
            else
                statement.setNull(6, Types.INTEGER);


            int newTweetId;
            synchronized (this) {
                statement.executeUpdate();
                statement = con.prepareStatement("SELECT last_insert_id() FROM tweets");
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                newTweetId = resultSet.getInt(1);
            }
            extractHashtags(content, newTweetId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return OutputType.SUCCESS;

    }
    public OutputType addRetweet(int retweetedId, String userName){


        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {

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

            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,NULL,?,NULL,NULL,?, NULL, DEFAULT)");
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
            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,?,NULL,NULL,?,?,NULL, DEFAULT)");
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

        Timestamp tweetDate = Timestamp.valueOf(LocalDateTime.now());

        PreparedStatement statement = null;
        try {
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


            statement = con.prepareStatement("INSERT INTO twitter.tweets VALUES(DEFAULT,?,?,?,?,?,?,NULL,NULL,NULL,?,?,DEFAULT)");
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

    public ArrayList<Retweet> getRetweets(int tweetId, String username){
        ArrayList<Retweet> retweets = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("""
                        SELECT id
                        FROM twitter.tweets\s
                        WHERE  retweeted_id = ?\s""");

            statement.setInt(1, tweetId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Retweet retweet = (Retweet) getTweet(result.getInt(1), username);
                retweet.setTweetId(result.getInt(1));
                retweets.add(retweet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retweets;
    }

    public ArrayList<Reply> getReplies(String usernameToShow,int tweetToShowId){
        ArrayList<Reply> replies = new ArrayList<>();
        PreparedStatement statement;
        try {
            statement = con.prepareStatement("SELECT user_id FROM twitter.tweets WHERE id = ?");
            statement.setInt(1, tweetToShowId);
            ResultSet result = statement.executeQuery();
            result.next();
            String tweetUsername = getUserFromId(result.getInt(1)).getUserName();
            statement = con.prepareStatement("""
                    SELECT id
                    FROM twitter.tweets\s
                    WHERE  replied_to = ?\s""");
            //TODO: add the parent reply to usernames to the set

            statement.setInt(1, tweetToShowId);
            result = statement.executeQuery();

            while (result.next()) {
                // get all replies to this tweet
                int reply_id = result.getInt(1);
                Reply reply = (Reply) getTweet(reply_id, usernameToShow);
                reply.addRepliedToUsername(tweetUsername);
                replies.add(reply);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        // get all replies to this tweet's retweets
        for (Retweet retweet:getRetweets(tweetToShowId, usernameToShow)){
            for (Reply reply : getReplies(usernameToShow, retweet.getTweetId())) {
                reply.addRepliedToUsername(retweet.getUserName());
                replies.add(reply);
            }
        }

        return replies;
    }

    private boolean checkIfLiked(int tweetId, String username) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT user_id FROM twitter.likes WHERE tweet_id=?");
        statement.setInt(1, tweetId);
        ResultSet result = statement.executeQuery();

        int userId = getUserId(username);
        while (result.next()) {
            if (result.getInt(1) == userId) {
                return true;
            }
        }
        return false;
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
