package client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    public String username;


    public Client(){
        // default constructor
    }

    public Client(Socket socket, PrintWriter out, BufferedReader in, String username){
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.username = username;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (socket == null) {
            try{
                socket = new Socket(serverAddress, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                userInput = new BufferedReader(new InputStreamReader(System.in));
                
                System.out.println("Connected to server on " + serverAddress + ":" + serverPort);
                
                // setting reception of messages onto a thread for concurrent listening

                
            } catch (IOException e) {
                System.out.println("ERROR: Unable to connect to server");
                e.printStackTrace();
            }
        }
        
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

        // Text 
        primaryStage.setTitle("OpenChat: " + username);

        
        Messages = FXCollections.observableArrayList();
        MessageListView = new ListView<>(Messages);
        MessageListView.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #cccccc;");
        
        // username header
        Label headerLabel = new Label("Welcome, " + username);
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 5;");
        
        MessageTextArea = new TextArea();
        MessageTextArea.setPromptText("Type your message here...");
        MessageTextArea.setPrefRowCount(3);
        
        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color:rgb(212, 224, 212); -fx-text-fill: black;");
        
        // What happens when the send button is clicked
        sendButton.setOnAction(event -> {
            String message = MessageTextArea.getText();
            if (!message.isEmpty()) {
                Messages.add("Me: " + message); 
                MessageTextArea.clear();
                out.println(message);
            }
        });

        // initialization config

        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(MessageTextArea, sendButton);
        inputBox.setPadding(new Insets(5));

        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(headerLabel, MessageListView, inputBox);
        root.setStyle("-fx-background-color:rgba(67, 164, 255, 0.99);");

        // The box/window for the application
        Scene scene = new Scene(root, 575, 350);
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