package org.mypomodoro.db.mysql;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loader for MySQL config properties file
 *
 * ./ - the root directory where the program is located
 * |__ mypomodoro.jar
 * |__ mysql.properties
 * 
 */
public class MySQLConfigLoader {
    
    private static final Properties properties = new Properties();
    
    public static void loadProperties() throws IOException {                       
        String path = "./mysql.properties";
        FileInputStream file = new FileInputStream(path);
        properties.load(file);
        file.close();
    }
    
    public static boolean isValid() {
        return  getPwd() != null && getUID() != null && getServer() != null && getDatabase() != null;
    }
    
    public static String getPwd() { // may be empty
        return  properties.getProperty("pwd");
    }
    
    public static String getUID() { // eg root
        return properties.getProperty("uid");        
    }
    
    public static String getServer() { // eg 127.0.0.1
        return properties.getProperty("server");        
    }
    
    public static String getDatabase() { // eg pomodoro
        return properties.getProperty("database");        
    }
}
