import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseManager manager = DatabaseManager.getManager();

//        manager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));
        Thread clientHandler=new ClientHandler(manager.getUser("mmd"));
        clientHandler.start();



    }
}
