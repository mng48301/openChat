package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;


public class Login extends Application {

    private String serverAddress = "localhost";
    private int serverPort = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void start(Stage primaryStage) {
        // Create a GridPane layout for arranging the UI components
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label sceneTitle = new Label("Welcome to OpenChat!");
        sceneTitle.setStyle("-fx-font-size: 20px;");
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("User Name:");
        grid.add(userNameLabel, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        // Password label and password field
        Label pwLabel = new Label("Password:");
        grid.add(pwLabel, 0, 2);

        PasswordField pwField = new PasswordField();
        grid.add(pwField, 1, 2);

        // Login button placed in an HBox for right alignment
        Button btn = new Button("Login");
        grid.add(btn, 1, 3);

        // Text to display messages to the user
        Text actionTarget = new Text();
        grid.add(actionTarget, 1, 4);

        // Set an action for the login button
        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwField.getText();

            // Example validation: username must be "admin" and password "password"
            if (authenticateWithServer(username, password)){
                actionTarget.setText("Login successful!");
                
                // Proceed to Client UI...
                Client client = new Client(socket, out, in, username);
                client.start(new Stage());
                primaryStage.close();
            } else {
                actionTarget.setText("Invalid username or password.");
                
            }
        });

        // Add a Register button
        Button registerBtn = new Button("Register");
        grid.add(registerBtn, 0, 3);

        // Set an action for the register button
        registerBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                actionTarget.setText("Username and password cannot be empty.");
                return;
            }

            if (registerWithServer(username, password)) {
                actionTarget.setText("Registration successful! You can now login.");
            } else {
                actionTarget.setText("Registration failed. Username may already exist.");
            }
        });

        // Create the scene and display it on the stage
        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean authenticateWithServer(String username, String password) {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in.readLine(); // Read the initial message from the server
            out.println("LOGIN REQUEST " + username + ":" + password);

            String response = in.readLine();
            return response != null && response.equals("LOGIN SUCCESSFUL!");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } 
    }

    private boolean registerWithServer(String username, String password) {
        try {
            Socket regSocket = new Socket("localhost", 12345);
            PrintWriter regOut = new PrintWriter(regSocket.getOutputStream(), true);
            BufferedReader regIn = new BufferedReader(new InputStreamReader(regSocket.getInputStream()));
            
            regIn.readLine(); // Read the initial message from the server
            regOut.println("REGISTER REQUEST " + username + ":" + password);
            
            String response = regIn.readLine();
            regSocket.close();
            
            return response != null && response.equals("REGISTRATION SUCCESSFUL!");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
