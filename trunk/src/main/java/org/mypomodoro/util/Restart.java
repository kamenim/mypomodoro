package org.mypomodoro.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URLDecoder;
import java.util.List;
import org.mypomodoro.Main;

/**
 * Restart application
 * Code found at http://java.dzone.com/articles/programmatically-restart-java
 * Code modified to support non Hotspot VM implementation, EXE wrapper file
 * Also, support for spaces and special characters in the path
 * 
 * @author Phil Karoo
 */
public class Restart {

    /** 
     * Sun property pointing the main class and its arguments. 
     * Might not be defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";

    /**
     * Restart the current Java application
     * @param runBeforeRestart some custom code to be run before restarting
     * @throws IOException
     */
    public static void restartApplication(Runnable runBeforeRestart) throws IOException {
        try {
            // java binary
            String java = System.getProperty("java.home") + "/bin/java";
            // vm arguments
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                // if it's the agent argument : we ignore it otherwise the
                // address of the old application and the new one will be in conflict
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            // init the command to execute, add the vm args
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

            // program main and program arguments
            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
            String pathFile = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            pathFile = URLDecoder.decode(pathFile, "UTF-8"); // Spaces and special charaters decoding                        

            if (pathFile.endsWith(".exe")) { // EXE wrapper
                cmd.append("-jar " + "\"" + new File(pathFile) + "\"");
            } else if (mainCommand != null && !mainCommand[0].isEmpty()) { // Hotspot VM implementation
                if (pathFile.endsWith(".jar")) { // Jar file                   
                    cmd.append("-jar " + "\"" + new File(pathFile) + "\"");
                } else { // Class file (running in IDE like Netbeans)
                    cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
                    // Program arguments
                    for (int i = 1; i < mainCommand.length; i++) {
                        cmd.append(" ");
                        cmd.append(mainCommand[i]);
                    }
                }
            } else { // Non Hotspot VM implementation
                cmd.append("-jar " + "\"" + new File(pathFile) + "\"");
            }

            // execute the command in a shutdown hook, to be sure that all the
            // resources have been disposed before restarting the application
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    }
                    catch (IOException e) {
                    }
                }
            });
            // execute some custom code before restarting
            if (runBeforeRestart != null) {
                runBeforeRestart.run();
            }
            // exit
            System.exit(0);
        }
        catch (Exception e) {
            // something went wrong
            throw new IOException("Error while trying to restart the application", e);
        }
    }
}