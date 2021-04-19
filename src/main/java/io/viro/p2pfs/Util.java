package io.viro.p2pfs;

/**
 * Util class for stdout.
 */
public class Util {
    public static void print(String message) {
        System.out.println(message);
    }

    public static void println(String message) {
        System.out.println("\n" + message);
    }

    public static void printWUS(String message) {
        message = message.replace("_", " ");
        System.out.println(message);
    }
}
