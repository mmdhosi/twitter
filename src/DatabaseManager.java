import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseManager {
    private static Connection con;
    private static DatabaseManager manager;
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
            User out = new User(
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getString(7),
                    result.getString(8),
                    result.getString(9));

            return out;

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



}
