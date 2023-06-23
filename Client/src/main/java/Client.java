import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Client {


    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        User user = new User();
//        user.setUserName("mh.fayaz");
//        user.setBirthdate("2003-10-21");
//        user.setFirstName("Mohammad Hosein");
//        user.setLastName("Fayaz");
//        user.setPassword("9876");
//        user.setEmail("mh82fayyaz@gmail.com");

//        Gson gson = new Gson();
//        user.setUserName("vex");
//        user.setPassword("1234");
//        String jsonRequest = gson.toJson(user);
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest getRequest = HttpRequest.newBuilder()
//                .uri(new URI("http://localhost:8000/login"))
//                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
//                .build();
//        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

//        HttpRequest getRequest = HttpRequest.newBuilder()
//                .uri(new URI("http://localhost:8000/user/mmd"))
//                .GET()
//                .header("authorization", "invalid_jwt")
//                .build();
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println(getResponse);

        Requester requester = Requester.login("mmd", "1234");




    }
}

