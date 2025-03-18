package client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create controls
        Label label = new Label("Hello JavaFX!");
        Button button = new Button("Click Me!");
        
        // Add action to button
        button.setOnAction(e -> label.setText("Button clicked!"));
        
        // Create layout
        VBox root = new VBox(10); // 10 is spacing between elements
        root.getChildren().addAll(label, button);
        
        // Create scene
        Scene scene = new Scene(root, 300, 200);
        
        // Set up stage
        primaryStage.setTitle("My First JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
