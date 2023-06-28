package com.mytwitter.client.controllers;

import com.mytwitter.tweet.Quote;
import com.mytwitter.tweet.Retweet;
import com.mytwitter.tweet.Tweet;
import com.mytwitter.user.UserProfile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

public class SearchListController implements Initializable {

    @FXML
    ListView<UserProfile> listView;

    @FXML
    ArrayList<UserProfile> profiles;

    public SearchListController(Stage stage, ArrayList<UserProfile> profiles) {
        this.profiles = profiles;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/searchlist-view.fxml"));
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private InputStream convertImageToStream(String img){
        if(img == null){
            // return default profile
            try {
                return new FileInputStream("profiles/king.jpg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new ByteArrayInputStream(Base64.getDecoder().decode(img));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setStyle("-fx-control-inner-background: #FFFFFF;");

        ObservableList<UserProfile> observableList = FXCollections.observableArrayList();
        observableList.setAll(profiles);
        listView.setItems(observableList);

        listView.setCellFactory(new Callback<ListView<UserProfile>, ListCell<UserProfile>>() {
            @Override
            public ListCell<UserProfile> call(ListView<UserProfile> cardModelListView) {
                return new ListCell<UserProfile>() {
                    private Button usernameButton = new Button();
                    private Label nameLabel = new Label();
                    private ImageView profileImg = new ImageView();

                    @Override
                    protected void updateItem(UserProfile profile, boolean empty) {
                        super.updateItem(profile, empty);
                        if (!empty && profile != null) {
                            //TODO: get profile image

                            profileImg.setImage(new Image(convertImageToStream(profile.getAvatar())));
                            float radius = 20;
                            profileImg.setFitHeight(radius);
                            profileImg.setFitWidth(radius);
                            Circle clip = new Circle(radius/2,radius/2,radius/2);
                            profileImg.setClip(clip);

                            nameLabel.setText(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());


                            usernameButton.setText(profile.getUser().getUserName());
                            usernameButton.setStyle("-fx-padding: 3");
//                            usernameButton.setOnAction(new EventHandler<ActionEvent>() {
//                                @Override
//                                public void handle(ActionEvent event) {
//                                    currentStage.hide();
//
//
//                                    Scene profileScene = null;
//                                    FXMLLoader profileLoader = null;
//                                    try {
//                                        profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile-view.fxml"));
//                                        profileScene = new Scene(profileLoader.load());
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Stage profileStage = new Stage();
//                                    ProfileController controller = profileLoader.getController();
//                                    profileStage.setScene(profileScene);
//                                    controller.setCurrentStage(profileStage);
//                                    profileStage.show();
//
//                                }
//                            });



                            HBox hBox = new HBox(profileImg, nameLabel);
                            hBox.setSpacing(20);
                            setGraphic(new VBox(hBox, usernameButton));

                            getStyleClass().add("fx-cell-size: 50px;");
//                            CardController card = new CardController();
//                            card.updateDetails(item.getName(), item.getContent());
//                            setGraphic(card.getRootVBox());
//
//                            setStyle("-fx-border-color: black;");
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }
}
