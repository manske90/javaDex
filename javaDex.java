import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

// Main program
public class javaDex extends Application {

    public javaDex() {
    }

    // Init method
    @Override
    public void init() {
        // Code to run before launching window
    }

    // Start method
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load and build the GUI
        Parent root =
                FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/javaDexGUI.fxml")));
        Scene primaryScene = new Scene(root);

        primaryStage.setTitle("JavaDex");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    // Main method
    public static void main(String[] args)
    {
        launch(args);
    }

}
