package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.tweet.RequestTweet;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.util.OutputType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TweetViewController implements Initializable {

    @FXML
    private Button addImageButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView imageView;

    @FXML
    private TextArea textArea;

    @FXML
    private Button tweetButton;

    @FXML
    private VBox imageViewBox;

    private File selectedImage = null;

    private final Stage tweetStage;
    private final Requester requester;

    public TweetViewController(Requester requester) {
        this.requester = requester;
        tweetStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tweet-view.fxml"));
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load());
            tweetStage.setScene(scene);
            tweetStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void cancelAction(ActionEvent event){
        tweetStage.hide();
    }

    @FXML
    private void addImageAction(ActionEvent event){
        FileChooser imageFileChooser = new FileChooser();
        imageFileChooser.setTitle("Choose your image file");
        selectedImage = imageFileChooser.showOpenDialog(null);
        if(selectedImage != null){
            Image image = new Image("file:"+selectedImage.getAbsolutePath());
            imageView.setImage(image);
            double width = imageView.getFitWidth();
            System.out.println(width);
            AnchorPane.setLeftAnchor(imageView, (600-width)/2);
            AnchorPane.setBottomAnchor(textArea, imageView.getFitHeight() + 120);
        }

    }
    @FXML
    private void tweetAction(ActionEvent event){
        try {
            String imageBase64 = null;
            if (selectedImage != null)
                imageBase64 = ImageBase64.convertToBase64(new FileInputStream(selectedImage));

            RequestTweet requestTweet = new RequestTweet();
            requestTweet.setContent(textArea.getText());
            requestTweet.setImage(imageBase64);
            OutputType result = requester.regularTweet(requestTweet);
            if(result == OutputType.SUCCESS){
                tweetStage.hide();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageView.setPreserveRatio(true);
    }
}
