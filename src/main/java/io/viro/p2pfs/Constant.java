package io.viro.p2pfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Constants.
 */
public class Constant {
    public static final String REG = "REG";
    public static final String UNREG = "UNREG";
    public static final String JOIN = "JOIN";
    public static final String SEPARATOR = " ";
    public static final String SEARCH = "SEARCH";
    public static final String LEAVE = "LEAVE";
    public static final String CHECK_NODE = "CHECK_NODE";


    //Responses
    public static final String REGOK = "REGOK";
    public static final String UNREGOK = "UNREGOK";
    public static final int COMMAND_ERROR = 9999;
    public static final int ALREADY_REGISTERED_TO_ME = 9998;
    public static final int ALREADY_REGISTERED = 9997;
    public static final int BS_FULL = 9996;
    public static final int UNREG_SUCCESS = 0;
    public static final int UNREG_ERROR = 9999;


    public static final String JOINOK = "JOINOK";
    public static final int JOIN_SUCCESS = 0;
    public static final int JOIN_ERROR = 9999;

    public static final String SEARCHOK = "SEARCHOK";
    private static final List<Integer> REG_ERROR_CODES;
    public static final int SEARCH_ERROR = 9998;

    public static final String NODEOK = "NODEOK";
    public static final int LEAVE_SUCCESS = 0;

    public static final String LEAVEOK = "LEAVEOK";
    public static final int NODE_ALIVE = 0;

    //Files
    private static final List<String> FILES;
    private static final List<String> QUERIES;
    private static final Random random = new Random();

    public static final int MAX_HOP_COUNT = 4;

    static {
        REG_ERROR_CODES = Arrays.asList(COMMAND_ERROR, ALREADY_REGISTERED, ALREADY_REGISTERED_TO_ME, BS_FULL);
        FILES = Arrays.asList("Adventures of Tintin", "Jack and Jill", "Glee",
                "The Vampire Diarie", "King Arthur", "Windows XP", "Harry Potter",
                "Kung Fu Panda", "Lady Gaga", "Twilight", "Windows 8",
                "Mission Impossible", "Turn Up The Music", "Super Mario",
                "American Pickers", "Microsoft Office 2010", "Happy Feet",
                "Modern Family", "American Idol", "Hacking for Dummies");

        QUERIES = Arrays.asList("Twilight", "Jack",
                "American Idol", "Happy Feet", "Twilight saga", "Happy Feet",
                "Happy Feet", "Feet", "Happy Feet", "Twilight", "Windows",
                "Happy Feet", "Mission Impossible", "Twilight", "Windows 8",
                "The", "Happy", "Windows 8", "Happy Feet", "Super Mario",
                "Jack and Jill", "Happy Feet", "Impossible", "Happy Feet",
                "Turn Up The Music", "Adventures of Tintin", "Twilight saga",
                "Happy Feet", "Super Mario", "American Pickers", "Microsoft Office 2010",
                "Twilight", "Modern Family", "Jack and Jill", "Jill", "Glee",
                "The Vampire Diarie", "King Arthur", "Jack and Jill", "King Arthur",
                "Windows XP", "Harry Potter", "Feet", "Kung Fu Panda", "Lady Gaga",
                "Gaga", "Happy Feet", "Twilight", "Hacking", "King");
    }

    public static List<Integer> getRegErrorCodes() {
        return REG_ERROR_CODES;
    }

    public static List<String> getFilesRand() {
        List<String> randFiles = new ArrayList<>();
//        int randNum = random.nextInt(3) + 5;
        int randNum = 7;
        int count = 0;
        while (count < randNum) {
            int index = random.nextInt(FILES.size());
            if (!randFiles.contains(FILES.get(index))) {
                randFiles.add(FILES.get(index));
                count++;
            }
        }
        return randFiles;
    }

    public static List<String> getQueriesRand() {
        List<String> randQueries = new ArrayList<>();
        int randNum = 7;
        int count = 0;
        while (count < randNum) {
            int index = random.nextInt(QUERIES.size());
            if (!randQueries.contains(QUERIES.get(index))) {
                randQueries.add(QUERIES.get(index).replace(" ", "_"));
                count++;
            }
        }
        return randQueries;
    }
}
