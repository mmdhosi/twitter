package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.tweet.Bio;
import com.mytwitter.user.User;
import com.mytwitter.user.UserProfile;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.util.StringResource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditViewController implements Initializable {
    @FXML
    private Button avatarLocationButton;

    @FXML
    private TextField avatarLocationField;

    @FXML
    private TextArea bioTextArea;

    @FXML
    private DatePicker birthdateField;

    @FXML
    private ChoiceBox<String> countryField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstnameField;

    @FXML
    private Button headerLocationButton;

    @FXML
    private TextField headerLocationField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField locationField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField webAddressField;

    private final int MAX_CHARACTERS = 160;

    private Stage currentStage;
    private UserProfile profile;
    private Requester requester = Requester.getRequester();

    File selectedAvatar;
    File selectedHeader;

    public EditViewController(Stage currentStage, UserProfile profile) {
        this.currentStage = currentStage;
        this.profile = profile;

        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/edit-view.fxml"));
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
        // set max characters
        bioTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > MAX_CHARACTERS) {
                bioTextArea.setText(newValue.substring(0, MAX_CHARACTERS));
            }
        });

        User user = profile.getUser();
        usernameField.setText(user.getUserName());
        firstnameField.setText(user.getFirstName());
        lastnameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneNumberField.setText(user.getPhoneNumber());
        passwordField.setText(user.getPassword());

        ObservableList<String> items = FXCollections.observableArrayList();
        items.setAll(StringResource.countries);
        countryField.setItems(items);
        countryField.setValue(user.getCountry());

        birthdateField.setValue(LocalDate.parse(user.getBirthdate()));

        Bio bio = profile.getBio();
        locationField.setText(bio.getLocation());
        webAddressField.setText(bio.getWebAddress());
        bioTextArea.setText(bio.getText());

        avatarLocationButton.setOnAction(event -> {
            FileChooser imageFileChooser = new FileChooser();
            imageFileChooser.setTitle("Choose your image file");
            selectedAvatar = imageFileChooser.showOpenDialog(null);
            avatarLocationField.setText(selectedAvatar.getAbsolutePath());
        });

        headerLocationButton.setOnAction(event -> {
            FileChooser imageFileChooser = new FileChooser();
            imageFileChooser.setTitle("Choose your image file");
            selectedHeader = imageFileChooser.showOpenDialog(null);
            headerLocationField.setText(selectedHeader.getAbsolutePath());
        });


    }

    @FXML
    private void submitAction(ActionEvent event){
        UserProfile editedProfile = new UserProfile();
        User editedUser = new User(usernameField.getText(),
                firstnameField.getText(),
                lastnameField.getText(),
                emailField.getText(),
                phoneNumberField.getText(),
                passwordField.getText(),
                countryField.getValue(),
                String.valueOf(birthdateField.getValue()));

        Bio editedBio = new Bio(bioTextArea.getText(),
                locationField.getText(),
                webAddressField.getText());

        if(selectedAvatar != null){
            try {
                editedProfile.setAvatar(ImageBase64.convertToBase64(new FileInputStream(selectedAvatar)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(selectedHeader != null){
            try {
                editedProfile.setHeader(ImageBase64.convertToBase64(new FileInputStream(selectedHeader)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        editedProfile.setUser(editedUser);
        editedProfile.setBio(editedBio);
        requester.editProfile(editedProfile);
        new ProfileViewController(currentStage, profile.getUser().getUserName());

    }

    @FXML
    private void cancelAction(ActionEvent event){
        new ProfileViewController(currentStage, profile.getUser().getUserName());
    }
}
