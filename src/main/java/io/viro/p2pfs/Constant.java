package io.viro.p2pfs;

import java.util.Arrays;
import java.util.List;

/**
 * Constants.
 */
public class Constant {
    public static final String REG = "REG";
    public static final String JOIN = "JOIN";
    public static final String SEPARATOR = " ";
    public static final String SEARCH = "SEARCH";


    //Responses
    public static final String REGOK = "REGOK";
    public static final int COMMAND_ERROR = 9999;
    public static final int ALREADY_REGISTERED_TO_ME = 9998;
    public static final int ALREADY_REGISTERED = 9997;
    public static final int BS_FULL = 9996;

    public static final String JOINOK = "JOINOK";
    public static final int JOIN_SUCCESS = 0;
    public static final int JOIN_ERROR = 9999;

    public static final String SEARCHOK = "SEARCHOK";

    private static final List<Integer> REG_ERROR_CODES;

    static {
        REG_ERROR_CODES = Arrays.asList(COMMAND_ERROR, ALREADY_REGISTERED, ALREADY_REGISTERED_TO_ME, BS_FULL);
    }

    public static List<Integer> getRegErrorCodes() {
        return REG_ERROR_CODES;
    }
}
