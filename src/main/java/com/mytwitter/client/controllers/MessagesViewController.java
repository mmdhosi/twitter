package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.client.SeenChangeListener;
import com.mytwitter.client.TweetCell;
import com.mytwitter.direct.Message;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ProfileImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MessagesViewController implements Initializable {


    @FXML
    ListView<Message> listView;

    @FXML
    Button backButton;

    @FXML
    private TextField newMessageField;

    @FXML
    private Button sendButton;
    @FXML
    private Button refreshButton;

    private final Stage currentStage;
    private final String directUsername;
    private ObservableList<Message> observableList;


    public MessagesViewController(Stage currentStage, String directUsername) {
        this.currentStage = currentStage;
        this.directUsername = directUsername;

        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/messages-view.fxml"));
            loader.setController(this);
            scene = new Scene(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        currentStage.setScene(scene);
        currentStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Requester requester = Requester.getRequester();

        observableList = FXCollections.observableArrayList();
        observableList.setAll(requester.getAllMessages(directUsername));
        listView.setItems(observableList);

        UserProfile userProfile = requester.getProfile(Requester.getUsername());
        UserProfile directProfile = requester.getProfile(directUsername);

        backButton.setOnAction(event -> {
            new DirectViewController(currentStage);
        });

        refreshButton.setOnAction(event -> {
            observableList.setAll(requester.getAllMessages(directUsername));
            scrollToBottom();
        });


        for(Message message:observableList){
            if(!message.getSeen()){
//                lastSeenId = observableList.indexOf(message);
//                break;
                requester.setSeen(message.getId());
            }
        }

        // it should be final since if it's changed it will break the change listener
//        int finalLastSeenId = lastSeenId;

        listView.setCellFactory(messageListView -> {
            ListCell<Message> cell = new ListCell<>() {

            ImageView avatarView = new ImageView();
            Hyperlink fullNameLink = new Hyperlink();
            Label messageLabel = new Label();

            @Override
            protected void updateItem(Message message, boolean b) {
                super.updateItem(message, b);
                if (!b && message != null) {


                    messageLabel.setText(message.getContent());

                    String sender = message.getSender();
                    if (Objects.equals(sender, Requester.getUsername())) {
                        fullNameLink.setText(userProfile.getUser().getFullName());
                        Image avatarImage = ProfileImage.getAvatarImage(userProfile.getAvatar());
                        TweetCell.setProfileAvatar(avatarView, avatarImage, 15);

                        fullNameLink.setOnAction(event -> {
                            new ProfileViewController(currentStage, Requester.getUsername());
                        });
                    } else if (Objects.equals(sender, directUsername)) {
                        fullNameLink.setText(directProfile.getUser().getFullName());
                        Image avatarImage = ProfileImage.getAvatarImage(directProfile.getAvatar());
                        TweetCell.setProfileAvatar(avatarView, avatarImage, 15);

                        fullNameLink.setOnAction(event -> {
                            new ProfileViewController(currentStage, directUsername);
                        });
                    }
                    HBox infoBox = new HBox(avatarView, fullNameLink);
                    infoBox.setSpacing(7);

                    VBox messageBox = new VBox(infoBox, messageLabel);
                    messageBox.setSpacing(10);

                    setGraphic(messageBox);

                }
            }
        };
//            cell.itemProperty().addListener(new SeenChangeListener(observableList, finalLastSeenId));
            return cell;
        });

        scrollToBottom();

        sendButton.setOnAction(event -> {
            sendMessage();
        });

        newMessageField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                sendMessage();

            }
        });
    }
    public void scrollToBottom(){
        listView.scrollTo(observableList.size()-1);
    }

    public void sendMessage(){
        String content = newMessageField.getText();
        newMessageField.setText("");
        Requester.getRequester().sendMessage(directUsername, content);
        observableList.setAll(Requester.getRequester().getAllMessages(directUsername));
        scrollToBottom();
    }
}
