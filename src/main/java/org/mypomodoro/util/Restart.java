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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URLDecoder;
import java.util.List;

import org.mypomodoro.Main;

/**
 * Restart application Code found at
 * http://java.dzone.com/articles/programmatically-restart-java Code modified to
 * support non Hotspot VM implementation, EXE wrapper file Also, support for
 * spaces and special characters in the path
 *
 */
public class Restart {

    /**
     * Sun property pointing the main class and its arguments. Might not be
     * defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";

    /**
     * Restart the current Java application
     *
     * @param runBeforeRestart some custom code to be run before restarting
     * @throws IOException
     */
    public static void restartApplication(Runnable runBeforeRestart) throws IOException {
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) { //for Mac OS X .app, need to additionally seperate .jar from .app b/c .jar still won't restart on mac
            new RestartMac(0);
        } else {
            try {
                // java binary
                String java = System.getProperty("java.home") + "/bin/java";
                // vm arguments
                List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
                StringBuilder vmArgsOneLine = new StringBuilder();
                for (String arg : vmArguments) {
                    // if it's the agent argument : we ignore it otherwise the
                    // address of the old application and the new one will be in conflict
                    if (!arg.contains("-agentlib")) {
                        vmArgsOneLine.append(arg);
                        vmArgsOneLine.append(" ");
                    }
                }
                // init the command to execute, add the vm args
                final StringBuilder cmd = new StringBuilder("\"" + java + "\" " + vmArgsOneLine);

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
                            //System.err.println(cmd.toString());
                            Runtime.getRuntime().exec(cmd.toString());
                        } catch (IOException e) {
                        }
                    }
                });
                // execute some custom code before restarting
                if (runBeforeRestart != null) {
                    runBeforeRestart.run();
                }
                // exit
                System.exit(0);
            } catch (Exception e) {
                // something went wrong
                throw new IOException("Error while trying to restart the application", e);
            }
        }
    }
}
