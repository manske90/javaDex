import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;


// The Controller class populates the GUI with data and handles user commands
public class Controller implements Initializable {

    @FXML
    private ListView<String> filterList;

    private ObservableList<String> items = FXCollections.observableArrayList();

    @FXML
    private ListView<String> filterBoolList;

    private ObservableList<String> bools = FXCollections.observableArrayList();

    @FXML
    private Button runSearchButton;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private TilePane resultsPane;

    @FXML
    private GridPane detailsPane;

    @FXML
    private Label resultCountDisplay;

    @FXML
    private TextArea resultsTextPane;

    @FXML
    private Button stripResultsBtn;

    @FXML
    private Button updateButton;

    @FXML
    private Button copyButton;

    @FXML
    private TextField individualSearch;

    @FXML
    private Button findButton;

    // Controller class
    public Controller() throws Exception {

    }


    // Simple search by name

    @FXML
    void findPokemon(MouseEvent event) throws Exception {
        // Initialize variables
        ArrayList<pokeTile> resultTiles = new ArrayList<>(); // List of "pokeTiles" (button containing Pokemon info)
        String input = individualSearch.getText(); // contents of the individualSearch TextField

        // Build the SQL query
        String sql = "SELECT * FROM pokemon WHERE name LIKE ";
        sql += "'%" + input + "%' ";

        sql += "OR nameAlt LIKE ";
        sql += "'%" + input + "%' ";

        sql += "OR formName LIKE ";
        sql += "'%" + input + "%' ";

        sql += "OR natDexNum LIKE ";
        sql += "'%" + input + "%' ";

        // Finalize the SQL query
        sql += "AND id > 0;";

        // Print the statement to the console for testing/validation
        System.out.println(sql);

        // Now initialize a new Pokedex object to search through
        Pokedex thisDex = new Pokedex();

        // Pass the parameters into the Pokedex
        ArrayList<Pokemon> dexResults = thisDex.showResultTiles(sql, false);

        // dexTextResults is what will be shown in the details pane initially (text list of all returned Pokemon)
        String dexTextResults = "";

        // Loop through the results
        for(Pokemon p : dexResults)
        {
            // Get this Pokemon's values
            String[][] pValues = p.getValues();
            String pDetails = "";

            // Format the values
            for(int i = 0; i < 23; i++)
            {
                pDetails += pValues[i][0] + ": " + pValues[i][1] + "\n";
            }
            // For some reason it didn't like pDetails below, so give it a fancy new variable that works
            String finalDetails = pDetails;

            // Create a button (pokeTile) for each result
            pokeTile pkTile = new pokeTile(p);

            // When the pokeTile is clicked it updates the Details pane with specific info
            pkTile.showTile().setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                    // Clear the details pane before adding new info, set new text pane info, and enable update button
                    detailsPane.getChildren().clear();
                    resultsTextPane.setText(finalDetails);
                    updateButton.setDisable(false);

                    // Loop through the passed values to generate labels and text fields
                    for(int i = 0; i < pValues.length; i++)
                    {
                        detailsPane.add(new Label(pValues[i][0]), 0, i);
                        if(pValues[i][0] == "id")
                        {
                            TextField tf = new TextField(pValues[i][1]);
                            tf.setEditable(false);
                            detailsPane.add(tf, 1, i);
                        } else {
                            detailsPane.add(new TextField(pValues[i][1]), 1, i);
                        }
                    }
                }
            } );
            // Add it to the results output
            resultTiles.add(pkTile);
            // Continue to populate the text list
            dexTextResults += pValues[2][1] + "\n";
        }

        // Reset the results windows before displaying new results
        resultsTextPane.setText("");
        resultsPane.getChildren().clear();

        // Now display new results
        resultsTextPane.setText(dexTextResults);
        for(pokeTile b : resultTiles)
        {
            resultsPane.getChildren().add(b.showTile());
        }
        resultCountDisplay.setText(String.valueOf(dexResults.size()));
    }

    // Clears the individualSearch TextField on click
    @FXML
    void clearFind(MouseEvent event) {
        individualSearch.clear();
    }

    // The main logic of the program. Handles the data returned by the search button.
    @FXML
    void runFilters(MouseEvent event) throws Exception {

        // Initialize variables
        ArrayList<pokeTile> resultTiles = new ArrayList<>(); // List of "pokeTiles" (button containing Pokemon info)
        boolean shinyToggle = false; // Allows the images returned to be shiny or not depending on search

        ObservableList<String> allFilters; // A list of all possible filters
        ObservableList<String> allBools; // A list of all possible boolean toggles
        ObservableList<String> selectedFilters; // What filters the user chooses

        // Populate lists appropriately
        selectedFilters = filterList.getSelectionModel().getSelectedItems();
        allBools = filterBoolList.getItems();
        allFilters = filterList.getItems();

        // Tracks index of selected boolean toggles in the list. Important to match with selected filters.
        ObservableList<Integer> selectedBoolInd = filterBoolList.getSelectionModel().getSelectedIndices();

        // Setup for eventual SQL call
        String[][] sqlFilters = new String[selectedFilters.size()][2];

        // Pairs up all filters with all boolean toggles
        String[][] pairs = new String[14][2];
        for (int i = 0; i < allBools.size(); i++) {
            pairs[i][0] = allFilters.get(i);
            pairs[i][1] = allBools.get(i);
        }

        // The following chunks of logic took the longest time to figure out.
        // The program needs to handle the common scenario in which more filters are selected than boolean toggles.
        // It also has to make sure the selected (and implied) toggles actually "line up" with the selected filters.
        // If the first filter the "selected" list needs to be "TRUE" then the first toggle isn't selected for it.
        // If multiple filters are selected, and then a toggle is selected to be FALSE on the second filter,
        // then the "first" filter and the "first" toggle don't actually line up in a simple list of each.
        // Finally I figured out that starting with a full sized list of each (filters and toggles), their indices would
        // match - then looping through the toggles to flip them based on the selected indices list would effectively
        // match up filters and toggle values properly. Then it was just a matter of selecting the pairs of filters and
        // toggles to add to the SQL statement.

        // For every possible boolean toggle, check to see if it was selected. Then, if it was, set it as "FALSE"
        // in the list of all filter/toggle pairs. Otherwise set as "TRUE"
        // update pairs[i][1] (boolean value) where index of selected bool equals index of selected filter

        for (int i = 0; i < allBools.size(); i++) {
            if (selectedBoolInd.contains(i)) {
                pairs[i][1] = "FALSE";
            } else {
                pairs[i][1] = "TRUE";
            }
        }

        // Loop through the full list of paired filters/toggles
        for (int i = 0; i < pairs.length; i++)
        {
           // Then loop through only the SELECTED filters
           for(int j = 0; j < selectedFilters.size(); j++)
           {
               // If filter at j matches the pair at i
               if (pairs[i][0].equals(selectedFilters.get(j)))
               {
                   // Add it to the SQL statement with its paired toggle value
                   // System.out.println("Pair at index " + i + " contains " + selectedFilters.get(j));
                   sqlFilters[j][0] = pairs[i][0];
                   sqlFilters[j][1] = pairs[i][1];
               }
           }
        }
        // Now determine if one of the filters is to look for shiny Pokemon, and set that toggle
        for (int i = 0; i < sqlFilters.length; i++)
        {
            // System.out.println(sqlFilters[i][0] + " " + sqlFilters[i][1]);
            if(sqlFilters[i][0].contains("caughtShiny") || sqlFilters[i][0].contains("caughtGoShiny")
                    || sqlFilters[i][0].contains("shinyAvailable"))
            {
                shinyToggle = true;
            }
        }

        // Finally start to build the SQL query
        String sql = "SELECT * FROM pokemon WHERE ";

        // Add the filter/toggle pairs
        for(int j = 0; j < sqlFilters.length; j++)
        {
            sql += sqlFilters[j][0] + " = '" + sqlFilters[j][1] + "' AND ";
        }

        // Finalize the SQL query
        sql += "id > 0;";

        // Print the statement to the console for testing/validation
        System.out.println(sql);

        // Now initialize a new Pokedex object to search through
        Pokedex thisDex = new Pokedex();

        // Pass the parameters into the Pokedex
        ArrayList<Pokemon> dexResults = thisDex.showResultTiles(sql, shinyToggle);

        // dexTextResults is what will be shown in the details pane initially (text list of all returned Pokemon)
        String dexTextResults = "";

        // Loop through the results
        for(Pokemon p : dexResults)
        {
            // Get this Pokemon's values
            String[][] pValues = p.getValues();
            String pDetails = "";

            // Format the values
            for(int i = 0; i < 23; i++)
            {
                    pDetails += pValues[i][0] + ": " + pValues[i][1] + "\n";
            }
            // For some reason it didn't like pDetails below, so give it a fancy new variable that works
            String finalDetails = pDetails;

            // Create a button (pokeTile) for each result
            pokeTile pkTile = new pokeTile(p);

            // When the pokeTile is clicked it updates the Details pane with specific info
            pkTile.showTile().setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent arg0) {
                    // Clear the details pane before adding new info, set new text pane info, and enable update button
                    detailsPane.getChildren().clear();
                    resultsTextPane.setText(finalDetails);
                    updateButton.setDisable(false);

                    // Loop through the passed values to generate labels and text fields
                    for(int i = 0; i < pValues.length; i++)
                    {
                        detailsPane.add(new Label(pValues[i][0]), 0, i);
                        if(pValues[i][0] == "id")
                        {
                            TextField tf = new TextField(pValues[i][1]);
                            tf.setEditable(false);
                            detailsPane.add(tf, 1, i);
                        } else {
                            detailsPane.add(new TextField(pValues[i][1]), 1, i);
                        }
                    }
                }
            } );
            // Add it to the results output
            resultTiles.add(pkTile);
            // Continue to populate the text list
            dexTextResults += pValues[2][1] + "\n";
        }

        // Reset the results windows before displaying new results
        resultsTextPane.setText("");
        resultsPane.getChildren().clear();

        // Now display new results
        resultsTextPane.setText(dexTextResults);
        for(pokeTile b : resultTiles)
        {
            resultsPane.getChildren().add(b.showTile());
        }
        resultCountDisplay.setText(String.valueOf(dexResults.size()));

    }

    // stripResultsPane clears all results from the results and details panes
    @FXML
    void stripResultsPane(MouseEvent event) throws IOException {
        resultCountDisplay.setText("");
        resultsPane.getChildren().clear();
        resultsTextPane.setText("");
        individualSearch.setText("Search with text");
        detailsPane.getChildren().clear();
        updateButton.setDisable(true);
    }

    // Copies contents of resultsTextPane to clipboard
    @FXML
    void copyToClipboard(MouseEvent event) {
        // Access the system clipboard
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        // Grab text from results
        String s = resultsTextPane.getText();

        // Set clipboard
        content.putString(s);
        clipboard.setContent(content);
        System.out.println("Text copied to clipboard!");
    }

    // Initializes GUI with filter values
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Setup filters for GUI search
        filterList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        String[] values = new String[23];

        // Access DAO
        Pokedex thisDex = null;
        try {
            thisDex = new Pokedex();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Results count should be blank when initialized
        resultCountDisplay.setText("");

        // Disable the update button until it is needed
        updateButton.setDisable(true);

        // Populate filter list with column names from database
        try {
            assert thisDex != null;
            values = thisDex.getColumns();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        filterList.setItems(items);

        // Only add filters that are relevant to search with (Not going to search by name, just boolean conditions)
        items.add(values[1]);
        items.add(values[4]);
        items.add(values[2]);
        items.add(values[3]);
        items.add(values[8]);
        items.add(values[9]);
        items.add(values[10]);
        items.add(values[11]);
        items.add(values[12]);
        items.add(values[13]);
        items.add(values[14]);
        items.add(values[15]);
        items.add(values[16]);
        items.add(values[22]);

        // Allow multiple selections
        filterBoolList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Set all bools to show as false. Choosing one means FALSE, leaving unselected means TRUE
        filterBoolList.setItems(bools);
        for(int i = 0; i < items.size(); i++)
        {
            bools.add("FALSE");
        }

        // Some formatting
        detailsPane.setPadding(new Insets(5, 5, 5, 5));
        detailsPane.setAlignment(Pos.TOP_LEFT);
        detailsPane.setHgap(5);
        detailsPane.setVgap(5);

    }

    // This method executes when the "update" button is clicked
    @FXML
    void updatePokemon(MouseEvent event) throws Exception {
        // Values
        // Build lists of fields and values
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        // Prepare array to store both lists
        String[][] results = new String[detailsPane.getRowCount()][2];
        // Initialize
        String field = "";
        String value = "";

        // Loop through all items in the detailsPane (labels and text fields)
        for(int i = 0; i < detailsPane.getChildren().size(); i++)
        {
            // Each element is a "node" called child
            Node child = detailsPane.getChildren().get(i);

            // If it's a label, add it to fields
            if (child instanceof Label) {
                field = ((Label) child).getText();
                fields.add(field);

            }

            // If it's a TextField, add it to values
            if(child instanceof TextField)
            {
                value = ((TextField)child).getText();
                values.add(value);
            }
        }
        // Populate array with fields and values
        for(int i = 0; i < fields.size(); i++)
        {
            results[i][0] = fields.get(i);
            results[i][1] = values.get(i);
        }
        // Access DAO and run method to update DB
        Pokedex thisDex = new Pokedex();
        thisDex.updateMon(results);
    }
}
