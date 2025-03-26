package client;
import java.util.Stack;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


// Client class

public class Client extends Application {

    private ObservableList Messages;
    private ListView MessageListView;
    private TextArea MessageTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //Text on top of the window
        primaryStage.setTitle("OpenChat: Chat Away!");

        //Message history
        Messages = FXCollections.observableArrayList();
        MessageListView = new ListView<>(Messages);
        MessageTextArea = new TextArea();
        Button sendButton = new Button("Send");

        //What happens when the send button is clicked
        sendButton.setOnAction(event -> {
            String message = MessageTextArea.getText();
            if (!message.isEmpty()) {
                Messages.add("Me: " + message);
                MessageTextArea.clear();
            }
        });


        //Box for the input text (horizontal)
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(MessageTextArea, sendButton);

        //Box for the input text (vertical)
        VBox root = new VBox(10);
        root.getChildren().addAll(MessageListView, inputBox);
        root.setStyle("-fx-background-color:rgb(38, 0, 255);");

        //The box/window for the application
        Scene scene = new Scene(root, 550, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}