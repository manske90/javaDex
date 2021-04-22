import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// Models a button object that contains Pokemon information
public class pokeTile {
    private Button tile;

    public pokeTile(Pokemon p) {
        // Grab values from given Pokemon
        String[][] values = p.getValues();

        try
        {
            // If there is an image path passed...
            if(values[22][1] != "null") {
                // ...append a prefix and file extension
                String imgPath = "X:\\Pokemon\\javaDex\\src\\resources\\images\\"; // Set this to the right directory
                imgPath += values[22][1] + ".png";

                // Now grab and setup that image
                FileInputStream input = new FileInputStream(imgPath);
                Image image = new Image(input);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(90);
                imageView.setFitWidth(90);
                imageView.setPreserveRatio(true);

                // Instantiate the button
                tile = new Button();
                // And give it the image
                tile.setGraphic(imageView);
            } else
            {
                // Or just make a button without an image
                tile = new Button(values[2][1]);
            }
        } catch (FileNotFoundException e)
        {
            tile = new Button(values[2][1]);
        }

        // Stylize the button
        tile.setStyle("-fx-spacing: 10px");
        tile.setWrapText(true);
        tile.setAlignment(Pos.CENTER);
        tile.setMinSize(90, 120);
        tile.setMaxSize(90, 120);
        tile.setPrefSize(90, 120);



    }

    // Get method
    public Button showTile()
    {
        return tile;
    }
}
