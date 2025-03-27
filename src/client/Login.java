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

    @Override
    public void start(Stage primaryStage) {
        // Create a GridPane layout for arranging the UI components
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Add a title label at the top of the grid
        Label sceneTitle = new Label("Welcome to OpenChat!");
        sceneTitle.setStyle("-fx-font-size: 20px;");
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Username label and text field
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
        Button btn = new Button("OpenChat Login");
        grid.add(btn, 1, 3);

        // Text to display messages to the user
        Text actionTarget = new Text();
        grid.add(actionTarget, 1, 4);

        // Set an action for the login button
        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwField.getText();

            // Example validation: username must be "admin" and password "password"
            if ("user".equals(username) && "password".equals(password)) {
                actionTarget.setText("Login successful!");
            } else {
                actionTarget.setText("Login failed.");
            }
        });

        // Create the scene and display it on the stage
        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
