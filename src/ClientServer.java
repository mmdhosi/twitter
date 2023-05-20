
import java.util.regex.*;
import java.util.*;


public class ClientServer {
    DatabaseManager databaseManager=new DatabaseManager();


    public OutputType signup(String id,String name,String lastName,String email,String phoneNumber,String password,String country,String birthDate){
        //TODO: email and phone both should not be null
        //TODO: transfer email validation to client
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches())
            return OutputType.INVALID_EMAIL;


        User user=new User(id,name,lastName,email,phoneNumber,password,country,birthDate);
        OutputType out = databaseManager.addUser(user);


        return OutputType.SUCCESS;
    }

    public OutputType login(String userName, String password){
        User user=databaseManager.getUser(userName);
        if(user!=null){
            if(Objects.equals(user.getPassword(), password)){
                return OutputType.SUCCESS;
            }
            return OutputType.INVALID_PASSWORD;
        }
        return OutputType.NOT_FOUND;
    }
}
