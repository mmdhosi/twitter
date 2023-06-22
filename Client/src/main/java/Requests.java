import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Requests {
    private String jwt;
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();


    public static void signup(User user){
        String jsonRequest = gson.toJson(user);
        try {
            HttpRequest signupRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/signup"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(signupRequest, HttpResponse.BodyHandlers.ofString());
            // TODO: check response codes (duplicates)

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static Requests login(String username, String password){


        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        String jsonRequest = gson.toJson(user);
        String jwt;
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if(getResponse.statusCode() == 200)
                jwt = getResponse.headers().map().get("authorization").get(0);
            else
                return null;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return new Requests(jwt);
    }


    private Requests(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public List<Tweet> getTimeline(){
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/home"))
                    .GET()
                    .header("authorization", jwt)
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(getResponse.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getProfile(String username){
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8000/user"))
                    .GET()
                    .header("authorization", jwt)
                    .
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(getResponse.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
