package com.mytwitter.client.controllers;

import com.mytwitter.client.TweetCell;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ProfileImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.util.List;

public class UsersListController {

    public UsersListController(List<UserProfile> userProfiles) {

        Stage stage = new Stage();

        ListView<UserProfile> profileListView = new ListView<>();

        ObservableList<UserProfile> observableList = FXCollections.observableArrayList();
        observableList.setAll(userProfiles);
        profileListView.setItems(observableList);

        profileListView.setCellFactory(new Callback<ListView<UserProfile>, ListCell<UserProfile>>() {
            @Override
            public ListCell<UserProfile> call(ListView<UserProfile> userProfileListView) {
                return new ListCell<UserProfile>(){

                    ImageView avatarImageView = new ImageView();
                    Hyperlink fullnameLink = new Hyperlink();
                    Label usernameLabel = new Label();
                    Label bio = new Label();

                    @Override
                    protected void updateItem(UserProfile profile, boolean b) {
                        super.updateItem(profile, b);
                        if(!b && profile != null){
                            TweetCell.setProfileAvatar(avatarImageView, ProfileImage.getAvatarImage(profile.getAvatar()), 25);

                            User user = profile.getUser();
                            fullnameLink.setText(user.getFullName());
                            fullnameLink.setOnAction(event -> {
                                new ProfileViewController(stage, user.getUserName());
                            });

                            usernameLabel.setText("@" + user.getUserName());

                            bio.setText(profile.getBio().getText());

                            VBox infoBox = new VBox(fullnameLink, usernameLabel, bio);
                            infoBox.setSpacing(7);

                            HBox profileBox = new HBox(avatarImageView, infoBox);
                            profileBox.setSpacing(10);

                            profileBox.setAlignment(Pos.TOP_LEFT);

                            setGraphic(profileBox);
                        }

                    }
                };
            }
        });


        Scene scene = new Scene(profileListView);
        stage.setScene(scene);
        stage.show();

    }
}
