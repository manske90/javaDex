// Models a Pokemon
public class Pokemon {
    private int id; // Unique key
    private int natDexNum; // National Pokedex number (Currently 1-898!)
    private String name; // Basic name of the pokemon, including form
    private boolean isBaseForm; // If it's not an alternate version of a Pokemon, this is true (Currently 898 possible)
    private int baseFormID; // Same as the national dex number, can probably be removed

    // A maximum of one of the following booleans should ever be set to TRUE at once
    private boolean isMegaForm; // Mega Evolved
    private boolean isGMaxForm; // Gigantamaxed
    private boolean isAltForm; // Alt versions such as Costume Pikachu, Attack Form Deoxys, etc
    private boolean isRegionalForm; // Alolan or Galarian forms are set to TRUE

    private boolean isGenderForm; // Only used twice in the current database but could be expanded to include many more
    private boolean shinyAvailable; // Can the Pokemon ever be shiny legally? Some have never been officially released

    private boolean caught; // Have I ever caught it? Or, do I currently have at least one?
    private boolean caughtShiny; // Do I currently have a shiny version of it?

    private boolean inGo; // Can it be caught in Pokemon Go?
    private boolean caughtGo; // Have I caught it in Pokemon Go?
    private boolean caughtGoShiny; // Have I caught/do I have a shiny version in Pokemon Go?

    private boolean inHome; // Can it be stored in Pokemon Home?
    private boolean inSwSh; // Can it be caught in Sword/Shield?

    private String serebiiName; // Used to scrape serebii.net for data and images

    private String nameAlt; // Can be used to return a more readable version of the name
    private String formName; // Kind of redundant

    private String notes; // Leave notes on this record such as "Need a second copy" or whatever
    private String imgSrc; // Path to image, can probably be done dynamically without a database record

    // No arg constructor
    public Pokemon()
    {
        
    }

    // Full arg constructor
    public Pokemon(
        int pid,
        int pNatDexNum,
        String pName,
        String pNameAlt,
        boolean pIsBaseForm,
        int pBaseFormID,
        boolean pIsMegaForm,
        boolean pIsGMaxForm,
        boolean pIsAltForm,
        boolean pIsRegionalForm,
        boolean pIsGenderForm,
        boolean pShinyAvailable,
        boolean pCaught,
        boolean pCaughtShiny,
        boolean pInGo,
        boolean pCaughtGo,
        boolean pCaughtGoShiny,
        boolean pInHome,
        boolean pInSwSh,
        String pSerebiiName,
        String pFormName,
        String pNotes,
        String pImgSrc)
    {
        id = pid;
        natDexNum = pNatDexNum;
        name = pName;
        isBaseForm = pIsBaseForm;
        baseFormID = pBaseFormID;
        isMegaForm = pIsMegaForm;
        isGMaxForm = pIsGMaxForm;
        isAltForm = pIsAltForm;
        isRegionalForm = pIsRegionalForm;
        isGenderForm = pIsGenderForm;
        shinyAvailable = pShinyAvailable;
        caught = pCaught;
        caughtShiny = pCaughtShiny;
        inGo = pInGo;
        caughtGo = pCaughtGo;
        caughtGoShiny = pCaughtGoShiny;
        inHome = pInHome;
        inSwSh = pInSwSh;
        serebiiName = pSerebiiName;
        nameAlt = pNameAlt;        
        formName = pFormName;
        notes = pNotes;
        imgSrc = pImgSrc;
    }

    // Constructor accepting a String array
    public Pokemon(String[] values)
    {
        id = Integer.parseInt(values[0]);
        natDexNum = Integer.parseInt(values[1]);
        name = values[2];
        isBaseForm = Boolean.parseBoolean(values[3]);
        baseFormID = Integer.parseInt(values[4]);
        isMegaForm = Boolean.parseBoolean(values[5]);
        isGMaxForm = Boolean.parseBoolean(values[6]);
        isAltForm = Boolean.parseBoolean(values[7]);
        isRegionalForm = Boolean.parseBoolean(values[8]);
        isGenderForm = Boolean.parseBoolean(values[9]);
        shinyAvailable = Boolean.parseBoolean(values[10]);
        caught = Boolean.parseBoolean(values[11]);
        caughtShiny = Boolean.parseBoolean(values[12]);
        inGo = Boolean.parseBoolean(values[13]);
        caughtGo = Boolean.parseBoolean(values[14]);
        caughtGoShiny = Boolean.parseBoolean(values[15]);
        inHome = Boolean.parseBoolean(values[16]);
        inSwSh = Boolean.parseBoolean(values[17]);
        serebiiName = values[18];
        nameAlt = values[19];
        formName = values[20];
        notes = values[21];
        imgSrc = values[22];
    }

    // Returns 2d array of fields and values for the Pokemon
    public String[][] getValues()
    {
        String[] fields = new String[23];
        fields[0] = "id";
        fields[1] = "natDexNum";
        fields[2] = "name";
        fields[3] = "isBaseForm";
        fields[4] = "baseFormID";
        fields[5] = "isMegaForm";
        fields[6] = "isGMaxForm";
        fields[7] = "isAltForm";
        fields[8] = "isRegionalForm";
        fields[9] = "isGenderForm";
        fields[10] = "shinyAvailable";
        fields[11] = "caught";
        fields[12] = "caughtShiny";
        fields[13] = "inGo";
        fields[14] = "caughtGo";
        fields[15] = "caughtGoShiny";
        fields[16] = "inHome";
        fields[17] = "inSwSh";
        fields[18] = "serebiiName";
        fields[19] = "nameAlt";
        fields[20] = "formName";
        fields[21] = "notes";
        fields[22] = "imgSrc";

        String[] values = new String[23];
        values[0] = String.valueOf(id);
        values[1] = String.valueOf(natDexNum);
        values[2] = name;
        values[3] = String.valueOf(isBaseForm);
        values[4] = String.valueOf(baseFormID);
        values[5] = String.valueOf(isMegaForm);
        values[6] = String.valueOf(isGMaxForm);
        values[7] = String.valueOf(isAltForm);
        values[8] = String.valueOf(isRegionalForm);
        values[9] = String.valueOf(isGenderForm);
        values[10] = String.valueOf(shinyAvailable);
        values[11] = String.valueOf(caught);
        values[12] = String.valueOf(caughtShiny);
        values[13] = String.valueOf(inGo);
        values[14] = String.valueOf(caughtGo);
        values[15] = String.valueOf(caughtGoShiny);
        values[16] = String.valueOf(inHome);
        values[17] = String.valueOf(inSwSh);
        values[18] = serebiiName;
        values[19] = nameAlt;
        values[20] = formName;
        values[21] = notes;
        values[22] = imgSrc;

        String[][] result = new String[23][2];
        for(int i = 0; i < 23; i++)
        {
            result[i][0] = fields[i];
            result[i][1] = values[i];
        }
        return result;

    }

}
