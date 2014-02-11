package org.mypomodoro.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class Restarts Java Bundled Native Mac Applications with the .app
 * extension To use put the following where you want to call a restart:
 * <pre>
 *      RestartMac restart = new RestartMac(0);
 * </pre> Then put the following in your main() method to delete the files
 * created:
 * <pre>
 *      RestartMac restart = new RestartMac(1);
 * </pre>
 *
 * @author Paul Barton
 */
public class RestartMac {

    /* AppleScript */
    private final String restartScript = "tell application \"myPomodoro\" to quit\n"
            + "tell application \"System Events\"\n"
            + "repeat until not (exists process \"myPomodoro\")\n"
            + "delay 0.2\n"
            + "end repeat\n"
            + "end tell\n"
            + "tell application \"myPomodoro\" to activate";
    /* AppleScript FileName */
    private final File restartFile = new File("myPomodoroRestart.scpt");
    /* Created Application FileName
     * Is created when the AppleScript is Compiled
     */
    private final String restartApp = "myPomodoroRestart.app";
    /* String[] used to Compile AppleScript to Application */
    private final String[] osacompileString = new String[]{"/usr/bin/osacompile", "-o", restartApp, restartFile.toString()};
    /* String[] used to Open created Application */
    private final String[] openString = new String[]{"/usr/bin/open", restartApp};
    /*
     * String used to Delete created Application
     * VERY DANGEROUS IF THIS STRING IS CHANGED
     */
    private final String deleteString = "rm -rf " + restartApp;
    /* Compiles AppleScript to Application */
    private Process osacompile = null;
    /* Opens created Application */
    @SuppressWarnings("unused")
    private Process open = null;
    /* Deletes created Application */
    @SuppressWarnings("unused")
    private Process delete = null;
    /* Arguments for Constructor */
    @SuppressWarnings("unused")
    private final int argv;

    /**
     * Restarts YourApplication.app on Mac OS X
     *
     * @param argc
     */
    public RestartMac(int argv) {
        this.argv = argv;
        if (argv == 0) { //Use 0 when you call a restart, such as in FileMenuItem
            compileAppleScript();
            openApp();
        } else { //Use 1 in main, so on restart it removes the files created
            deleteScript();
            deleteApp();
        }
    }

    /*
     * Write AppleScript to a File
     */
    private void scriptToFile() {
        restartScript.replaceAll("\n", System.getProperty("line.separator"));
        try {
            BufferedWriter restartWriter = new BufferedWriter(new FileWriter(restartFile));
            restartWriter.write(restartScript);
            restartWriter.close();
        } catch (IOException ioe) {
            System.err.format("IOException: %s%n", ioe);
        }
    }

    /*
     * Compiles AppleScript to Application
     */
    private void compileAppleScript() {
        scriptToFile();
        try {
            osacompile = Runtime.getRuntime().exec(osacompileString);
            osacompile.waitFor(); //everything must wait until this process is completed
        } catch (InterruptedException ex) {
            Logger.getLogger(RestartMac.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestartMac.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Opens created Application
     */
    private void openApp() {
        try {
            open = Runtime.getRuntime().exec(openString);
        } catch (IOException ex) {
            Logger.getLogger(RestartMac.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Deletes AppleScript if found
     */
    private void deleteScript() {
        if (restartFile.exists() && restartFile.isFile()) {
            restartFile.delete();
        }
    }

    /*
     * Deletes Created Application if found
     */
    private void deleteApp() {
        try {
            delete = Runtime.getRuntime().exec(deleteString);
        } catch (IOException ex) {
            Logger.getLogger(RestartMac.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}