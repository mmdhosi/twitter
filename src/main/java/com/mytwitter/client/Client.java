package com.mytwitter.client;

import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Client {


    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
//        User user = new User("vexplitz", "846279513");
//
//        user.setBirthdate("2004-06-01");
//        user.setFirstName("Ali");
//        user.setLastName("Hasanyazdi");
//        user.setEmail("aliyazdihasan@gmail.com");

//
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println(getResponse);
//        Requester.signup(user);
        Requester requester = Requester.login("mmd", "1234");
        System.out.println(requester.getProfile("vex"));

//        System.out.println(requester.unblock("mh.fayaz"));
    }
}

