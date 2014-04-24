/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
 */
public class RestartMac {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

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
     * @param argv
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
        } catch (IOException ex) {
            logger.error("", ex);
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
            logger.error("", ex);
        } catch (IOException ex) {
            logger.error("", ex);
        }
    }

    /*
     * Opens created Application
     */
    private void openApp() {
        try {
            open = Runtime.getRuntime().exec(openString);
        } catch (IOException ex) {
            logger.error("", ex);
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
            logger.error("", ex);
        }
    }
}
