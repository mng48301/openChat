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
import server.Server;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

// Client class

public class Client extends Application {

    private ObservableList<String> Messages;
    private ListView<String> MessageListView;
    private TextArea MessageTextArea;

    // I/O stream config for sending and receiving messages
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader userInput;
    private String serverAddress = "localhost";
    private int serverPort = 12345;
    private Socket socket;

    public static void main(String[] args) {
        launch(args);

    

    }

    @Override
    public void start(Stage primaryStage) {
        try{
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Connected to server on " + serverAddress + ":" + serverPort);
            
            // setting reception of messages onto a thread for concurrent listening

            Thread listener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        final String message = serverMessage;
                        Platform.runLater(() -> {
                            Messages.add(message);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            listener.start();
        } catch (IOException e) {
            System.out.println("ERROR: Unable to connect to server");
            e.printStackTrace();
        }

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
                out.println(message);
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

    @Override
    public void stop() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.close();
                in.close();
                userInput.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}