package com.mytwitter.client;

import com.mytwitter.client.controllers.WelcomeViewController;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Client extends Application {


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
//        Requester requester = Requester.login("mmd", "1234");
//        System.out.println(requester.getProfile("vex"));

//        System.out.println(requester.unblock("mh.fayaz"));
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        WelcomeViewController controller = fxmlLoader.getController();
        controller.setCurrentStage(stage);
//
//        Label label = new Label("Hello, JavaFX!");
//        label.setLayoutX(20);
//        label.setLayoutY(20);
//
//        Button button = new Button("Click me!");
//        button.setLayoutX(20);
//        button.setLayoutY(60);
//        button.setOnAction(event -> {
//            label.setText("Button clicked!");
//        });
//
//        Pane pane = (Pane) scene.getRoot();
//        pane.getChildren().addAll(label, button);

        stage.setScene(scene);
        stage.setTitle("JavaFX Test");
        stage.show();
    }
}

