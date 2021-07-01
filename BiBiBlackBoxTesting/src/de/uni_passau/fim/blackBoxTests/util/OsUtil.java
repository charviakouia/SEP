package de.uni_passau.fim.blackBoxTests.util;

/**
 * Utility class for getting an OS.
 */
public class OsUtil {
    public enum OS {
        WINDOWS, LINUX, MAC
    };// Operating systems.

    private static OS os = null;

    /**
     * Gets an OS name of the user.
     */
    public static OS getMyOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                os = OS.LINUX;
            } else if (operSys.contains("mac")) {
                os = OS.MAC;
            }
        }
        return os;
    }
}
