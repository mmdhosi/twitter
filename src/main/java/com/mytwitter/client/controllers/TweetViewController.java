package com.mytwitter.client.controllers;

import com.mytwitter.client.Requester;
import com.mytwitter.poll.Answer;
import com.mytwitter.poll.Poll;
import com.mytwitter.tweet.RequestTweet;
import com.mytwitter.util.ImageBase64;
import com.mytwitter.util.OutputType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TweetViewController implements Initializable {

    private static final int MAX_OPTIONS = 6;
    @FXML
    private Button addImageButton;
    @FXML
    private Button addPollButton;

    @FXML
    private VBox pollBox;

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

    private int pollOptionsCount = 0;

    private final Stage tweetStage;
    private final Requester requester;

    private final int MAX_CHARACTERS = 280;

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
        addPollButton.setDisable(true);
        addImageButton.setDisable(true);

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
    private void addPollAction(ActionEvent event){
        pollBox.setVisible(true);
        addImageButton.setDisable(true);
        addPollButton.setDisable(true);
        AnchorPane.setBottomAnchor(textArea, pollBox.getHeight() + 120);


        createNewPollBox();
    }

    private HBox getNewOptionBox(int i){
        Label indexLabel = new Label(i+". ");
        TextField optionField = new TextField();
        optionField.setPromptText("Enter an option here:");
        optionField.setFont(new Font(14));
        optionField.setPrefWidth(300);

        HBox optionBox = new HBox(indexLabel, optionField);
        optionBox.setAlignment(Pos.CENTER_LEFT);
        optionBox.setSpacing(4);
        optionBox.setPadding(new Insets(4));
        return optionBox;
    }

    private void createNewPollBox(){
        TextField questionField = new TextField();
        questionField.setPromptText("Enter the poll question: ");
        questionField.setFont(new Font(14));

        pollBox.getChildren().add(questionField);

        VBox.setMargin(questionField, new Insets(5,0,5,0));

        Button addOptionButton = new Button("Add Option");
        HBox newOptionBox = new HBox(addOptionButton);
        newOptionBox.setAlignment(Pos.BOTTOM_RIGHT);
        pollBox.getChildren().add(newOptionBox);

        addOptionButton.setOnAction(event -> {
            pollBox.getChildren().remove(pollBox.getChildren().size()-1);
            pollBox.getChildren().add(getNewOptionBox(++pollOptionsCount));
            if(pollOptionsCount < MAX_OPTIONS)
                pollBox.getChildren().add(newOptionBox);
        });
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

            if(pollBox.isVisible()){
                Poll poll = new Poll();
                TextField questionField = (TextField) pollBox.getChildren().get(0);
                poll.setQuestion(questionField.getText());
                for(int i = 1; i < MAX_OPTIONS; i++){
                    Answer answer = new Answer();
                    HBox hBox= (HBox) pollBox.getChildren().get(i);
                    try {
                        if (hBox.getChildren().get(1) instanceof TextField answerField) {
                            answer.setAnswer(answerField.getText());
                            poll.addAnswer(answer);
                        } else
                            break;
                    } catch (IndexOutOfBoundsException e){
                        break;
                    }
                }
                requestTweet.setPoll(poll);
            }

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
        pollBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pollBox.setPadding(new Insets(10));
        pollBox.setVisible(false);

        // set character limit
        textArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.length() > MAX_CHARACTERS){
                textArea.setText(newValue.substring(0, MAX_CHARACTERS));
            }
        });

        imageView.setPreserveRatio(true);
    }
}
