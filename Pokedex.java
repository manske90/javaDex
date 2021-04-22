import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

// Pokedex serves as the Database Access Object, since a Pokedex is basically a database anyway!
public class Pokedex {

    // Setup MySQL database access
    final private Connection myConn;

    public Pokedex() throws Exception {
        System.out.print("Connecting to database... ");

        // Get db properties
        Properties props = new Properties();
        props.load(new FileInputStream("db.properties"));

        String user = props.getProperty("user");
        String pass = props.getProperty("password");
        String url = props.getProperty("dburl");

        // Connect to DB
        myConn = DriverManager.getConnection(url, user, pass);

        System.out.print("Connected!\n");

    }

    // Returns the column names of the database
    public String[] getColumns() throws SQLException {
        String[] values = new String[23];

        try (Statement st = myConn.createStatement()) {
            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA='javaDexDB' AND TABLE_NAME='pokemon';";
            ResultSet rs = st.executeQuery(sql);

            int e = 0;
            while (rs.next()) {
                String cName = rs.getString("COLUMN_NAME");
                values[e] = cName;
                e++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    // showResultTiles returns the data necessary to populate the GUI with the result buttons
    public ArrayList<Pokemon> showResultTiles(String sql, boolean shinyToggle)
    {
        // Initialize output variable
        ArrayList<Pokemon> output = new ArrayList<>();

        // Count results
        int count = 0;

        // Initializing a SQL statement
        try (Statement st = myConn.createStatement()) {
            // Execute the query passed by javaDex
            ResultSet rs = st.executeQuery(sql);

            // Loop through results
            while (rs.next()) {
                // Grab values for each result from query
                // A pain to set these up since one change to the database structure requires multiple code updates
                String[] values = new String[23];
                values[0] = String.valueOf(rs.getInt("id"));
                values[1] = String.valueOf(rs.getInt("natDexNum"));
                values[2] = rs.getString("name");
                values[3] = rs.getString("isBaseForm");
                values[4] = String.valueOf(rs.getInt("baseFormID"));
                values[5] = rs.getString("isMegaForm");
                values[6] = rs.getString("isGMaxForm");
                values[7] = rs.getString("isAltForm");
                values[8] = rs.getString("isRegionalForm");
                values[9] = rs.getString("isGenderForm");
                values[10] = rs.getString("shinyAvailable");
                values[11] = rs.getString("caught");
                values[12] = rs.getString("caughtShiny");
                values[13] = rs.getString("inGo");
                values[14] = rs.getString("caughtGo");
                values[15] = rs.getString("caughtGoShiny");
                values[16] = rs.getString("inHome");
                values[17] = rs.getString("inSwSh");
                values[18] = rs.getString("serebiiName");
                values[19] = rs.getString("nameAlt");
                values[20] = rs.getString("formName");
                values[21] = rs.getString("notes");
                String img = String.format("%03d", Integer.valueOf(values[1]));
                values[22] = img;
                // Add mega or g-max labels to filepath when necessary
                // Add regional tags when necessary
                // Add -shiny to the image path if a shiny filter was ran
                if (values[2].contains("alola")) {
                    values[22] += "-alola";
                }
                if (values[2].contains("galar")) {
                    values[22] += "-galar";
                }
                if ("TRUE".equalsIgnoreCase(values[5])) {
                    if (values[2].contains("mega-x")) {
                        values[22] += "-mx";
                    } else if (values[2].contains("mega-y")) {
                        values[22] += "-my";
                    } else {
                        values[22] += "-m";
                    }
                }
                if ("TRUE".equalsIgnoreCase(values[6])) {
                    values[22] += "-gi";
                }
                if (shinyToggle) {
                    values[22] += "-shiny";
                }

                // Now instantiate a new Pokemon for each result based on those values
                Pokemon mon = new Pokemon(values);

                // Add that Pokemon to the output list
                output.add(mon);

                // Increase counter
                count++;
            }

            // Catch exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log result count to console
        System.out.println("Results: " + count);

        return output;
    }

    // Method to allow database updates to be ran from the GUI
    public void updateMon(String[][] values) throws Exception
    {
        System.out.println("Passed to DAO");
        // Initialize SQL statement
        Statement st;

        // Start to build SQL
        String fieldString = "UPDATE pokemon SET ";
        for(int i = 0; i < values.length; i++)
        {
            System.out.println(values[i][0] + " " + values[i][1]);
            // Adds commas unless its the last iteration
            if(i == (values.length - 1))
            {
                fieldString += values[i][0] + " = " + "\"" + values[i][1] + "\"";
            } else
            {
                fieldString += values[i][0] + " = " + "\"" + values[i][1] + "\", ";
            }
        }
        // Finish building SQL statement
        fieldString += " WHERE id = " + values[0][1] + ";";

        // Execute SQL
        st = myConn.createStatement();
        System.out.println(st);
        st.executeUpdate(fieldString);

    }
    // setImgs can be ran to add paths to Pokemon images to the database
    public void setImgs() throws Exception
    {
        // Initialize SQL statement
        Statement st = myConn.createStatement();
        try {

            // This query can be changed manually to include more Pokemon to update images for
            String sql = "SELECT * FROM pokemon WHERE id > 0 AND id < 899";
            ResultSet rs = st.executeQuery(sql);

            // Loop through results
            while(rs.next())
            {
                // This could be used for testing if an image is already set
                // String pImg = rs.getString("imgSrc");

                // Change based on where images are stored
                String imgPath = "resources\\images\\";
                // Gets AND formats the national dex number (for the filepath)
                String pokeNum = String.format("%03d", rs.getInt("natDexNum"));
                // Just gets the unformatted national dex number (for the query)
                String pokeNum2 = String.valueOf(rs.getInt("natDexNum"));
                // Combine some variables
                String p1 = imgPath + pokeNum;

                // Execute the update using prepared statement
                PreparedStatement ut = myConn.prepareStatement("UPDATE pokemon SET imgSrc = ? WHERE id = ?");
                ut.setString(1, p1);
                ut.setString(2, pokeNum2);
                // Print the query for testing/validation purposes
                System.out.println(ut);
                ut.executeUpdate();

            }
        // Exception catching
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(st != null)
                    st.close();
            } catch (SQLException e1){
                e1.printStackTrace();
            }
            try {
                    myConn.close();
            } catch (SQLException e2){
                e2.printStackTrace();

            }
        }
    }
}
