import java.util.ArrayList;

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

    @Override
    public void run() {
        System.out.println(userSearch("mmd"));

    }
}
