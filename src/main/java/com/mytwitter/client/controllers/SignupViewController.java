package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.user.User;
import com.mytwitter.util.OutputType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupViewController {

    private Stage currentStage;

    @FXML
    private DatePicker birthdateField;


    @FXML
    private Label repeatPasswordError;

    @FXML
    private Label requiredError;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField passwordRepeatField;

    @FXML
    private TextField phonenumberField;

    @FXML
    private Button submitButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label passwordError;

    @FXML
    private Label usernameError;

    @FXML
    private Label emailError;

    @FXML
    private ChoiceBox<String> countryChoice;




    private void waitToFix(Alert alert){
        try {
            alert.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickOnCountryChoice(MouseEvent event){
        ObservableList<String> items = FXCollections.observableArrayList(   "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic (CAR)", "Chad", "Chile", "China", "Colombia", "Comoros", "Democratic Republic of the Congo", "Republic of the Congo", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini (formerly Swaziland)", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar (formerly Burma)", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia (formerly Macedonia)", "Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates (UAE)", "United Kingdom (UK)", "United States of America (USA)", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City (Holy See)", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe");
        countryChoice.setItems(items);
    }


    @FXML
    private void submitButtonAction(ActionEvent event) {
        //TODO: uniq bodan
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if (Objects.equals(usernameField.getText(), "")) {
            usernameError.setText("username is required");
            waitToFix(alert);
        }
        usernameError.setText("");


        if (Objects.equals(passwordField.getText(), "")) {
            passwordError.setText("password is required");
            waitToFix(alert);
        }
        else {
            passwordError.setText("");
            String pass = passwordField.getText();
            if (pass.length() < 8) {
                passwordError.setText("password must be 8 character at least");
                waitToFix(alert);
            }
            passwordError.setText("");
            if (!pass.matches(".*[A-Z].*")) {
                passwordError.setText("password must have capital characters");
                waitToFix(alert);
            }
            passwordError.setText("");
            if (!pass.matches(".*[a-z].*")) {
                passwordError.setText("password must have small characters");
                waitToFix(alert);
            }
            passwordError.setText("");
        }
        if (!Objects.equals(passwordField.getText(), passwordRepeatField.getText())){
            if(Objects.equals(repeatPasswordError.getText(), "")){
                repeatPasswordError.setText("repeated password is required");
                waitToFix(alert);
            }
            repeatPasswordError.setText("");
            repeatPasswordError.setText("repeated password is wrong");
            waitToFix(alert);
        }
        repeatPasswordError.setText("");
        if(Objects.equals(emailField.getText(), "") && Objects.equals(phonenumberField.getText(), "")){
            requiredError.setText("Email or Phone number is required");
            waitToFix(alert);
        }
        requiredError.setText("");

        String email = emailField.getText();
        if(!Objects.equals(email, "")) {
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                emailError.setText("email is invalid");
                waitToFix(alert);
            }
            emailError.setText("");
        }

        String username = usernameField.getText();
        String firstname= firstNameField.getText();
        String lastname=lastNameField.getText();
        String phonenumber=phonenumberField.getText();
        String password=passwordField.getText();
        String country=countryChoice.getValue();
        String birthdate= String.valueOf(birthdateField.getValue());
        //TODO: VERY IMPORTANT!!! set birthday to null (va baghie age hal dari)
        //TODO: * for password

        User user=new User(username,firstname,lastname,email,phonenumber,password,country,birthdate);
        OutputType out = Requester.signup(user);
        if(out == OutputType.DUPLICATE_USERNAME){
            //TODO: do the rest
        }

        currentStage.hide();

        Scene loginScene = null;
        FXMLLoader loginloader = null;
        try {
            loginloader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            loginScene = new Scene(loginloader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage loginStage = new Stage();
        LoginViewController controller = loginloader.getController();
        loginStage.setScene(loginScene);
        controller.setCurrentStage(loginStage);
        loginStage.show();
    }


    public void setStageCurrent(Stage stage){
        this.currentStage = stage;
    }


}