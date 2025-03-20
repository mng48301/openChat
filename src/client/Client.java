package client;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Scene;

// Client class

public class Client extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client");
        Button button = new Button("Test");
        button.setOnAction(e -> System.out.println("Test"));
        Scene scene = new Scene(button, 300, 300);
        primaryStage.setScene(scene);



        primaryStage.show();
    }
}