
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;


public class Server {
    static ExecutorService clientsThreads = Executors.newCachedThreadPool();
    static DatabaseManager databaseManager = DatabaseManager.getManager();
    static Gson gson = new Gson();

    final static String secret = "bW1kaG9zaSZhbGlAbWFkZSp0aGlzKmJ5X0pBVkE7DQo=";

    static Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
            SignatureAlgorithm.HS256.getJcaName());

    public static Jws<Claims> parseJwt(String jwtString) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }


    public static OutputType signup(User user){
        //TODO: email and phone both should not be null
        //TODO: transfer email validation to client

//        String regex = "^(.+)@(.+)$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        if(!matcher.matches())
//            return OutputType.INVALID_EMAIL;

        OutputType out = databaseManager.addUser(user);

        return OutputType.SUCCESS;
    }

    public static OutputType login(User userData){

        User user = databaseManager.getUser(userData.getUserName());
        if(user!=null){
            if(Objects.equals(user.getPassword(), userData.getPassword())){
                return OutputType.SUCCESS;
            }
            return OutputType.INVALID_PASSWORD;
        }
        return OutputType.NOT_FOUND;
    }

    public static void main(String[] args) {

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/signup", new SignupHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/home", new TimelineHandler());
            server.createContext("/user", new UserHandler());
            server.setExecutor(clientsThreads);
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        databaseManager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));
//        Thread clientHandler=new ClientHandler(databaseManager.getUser("mmd"));
//        clientHandler.start();



//        manager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));

    }

    static class LoginHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equals("POST")) {
                InputStream in = exchange.getRequestBody();
                byte[] request = in.readAllBytes();
                in.close();
                String requestJson = new String(request);
                User user = gson.fromJson(requestJson, User.class);
                OutputType output = login(user);

//                String jwt = Jwts.builder()
//                        .setSubject(user.getUserName())
//                        .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
//                        .compact();

                Instant now = Instant.now();
                String jwt = Jwts.builder()
                        .claim("username", user.getUserName())
                        .setSubject(user.getUserName())
                        .setId(UUID.randomUUID().toString())
                        .setIssuedAt(Date.from(now))
                        .signWith(hmacKey)
                        .compact();

                OutputStream os = exchange.getResponseBody();

                if(output.equals(OutputType.NOT_FOUND)){
                    exchange.sendResponseHeaders(404, 0);
                    os.write("user doesn't exist.".getBytes());
                } else if(output.equals(OutputType.INVALID_PASSWORD)){
                    exchange.sendResponseHeaders(401, 0);
                    os.write("your password is invalid".getBytes());
                } else {
                    exchange.getResponseHeaders().add("authorization", jwt);
                    exchange.sendResponseHeaders(200, 0);
                }
                os.close();
            }
        }
    }

    static class UserHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("GET")){
                InputStream in = exchange.getRequestBody();
                String requestJson = new String(in.readAllBytes());
                Request request = gson.fromJson(requestJson, Request.class);
                String username = request.getUsername();
                switch (request.getRequestType()){
                    case GET_PROFILE : {
                        UserProfile userProfile=new UserProfile();
                        userProfile.setUser(databaseManager.getUser(username));
                        userProfile.setAvatar(databaseManager.getAvatar(username));
                        userProfile.setHeader(databaseManager.getHeader(username));
                        userProfile.setTweets((ArrayList<Tweet>) databaseManager.getTweetsForUser(username));
                        break;
                    }


                }
            }
        }
    }

    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if(t.getRequestMethod().equals("POST")) {
                InputStream in = t.getRequestBody();
                byte[] request = in.readAllBytes();
                in.close();
                String requestJson = new String(request);
                User user = gson.fromJson(requestJson, User.class);
                OutputType output = signup(user);
                //TODO: JWT
                OutputStream os = t.getResponseBody();

                if(output.equals(OutputType.DUPLICATE_USERNAME)){
                    t.sendResponseHeaders(409, 0);
                    os.write("username already exists".getBytes());
                } else if(output.equals(OutputType.DUPLICATE_EMAIL)){
                    t.sendResponseHeaders(409, 0);
                    os.write("email already exists".getBytes());
                }else if(output.equals(OutputType.DUPLICATE_PHONENUMBER)){
                    t.sendResponseHeaders(409, 0);
                    os.write("phone number already exists".getBytes());
                } else {
                    t.sendResponseHeaders(200, 0);
                }
                os.close();
            }
        }
    }

    static class TimelineHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t){
            if(t.getRequestMethod().equals("GET")) {
                Jws<Claims> jwt =  parseJwt(t.getRequestHeaders().getFirst("authorization"));
                String currentUsername = (String) jwt.getBody().get("username");
                List<Tweet> tweets = databaseManager.getTimeline(currentUsername);

                OutputStream os = t.getResponseBody();
                String json = gson.toJson(tweets);
                try {
                    t.sendResponseHeaders(200, 0);
                    os.write(json.getBytes());
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
