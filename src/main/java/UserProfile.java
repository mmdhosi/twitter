import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class UserProfile {
    User user;
    int countFollowers,countFollowings;
    String header,avatar;
    ArrayList<Tweet> tweets;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    public void setHeader(Blob header) {
        this.header = convertImageToString(header);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = convertImageToString(avatar);
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }
}
