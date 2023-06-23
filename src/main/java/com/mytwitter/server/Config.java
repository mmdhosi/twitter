package com.mytwitter.server;

import java.io.*;
import java.util.Properties;

public class Config {

    private static Properties loadProperties(){
        FileInputStream configFile = null;
        try {
            configFile = new FileInputStream(".config");
        } catch (FileNotFoundException e) {
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try { configFile.close(); }  catch (IOException ignored) {}

        return properties;
    }

    public static String getDecryptionPass(){
        Properties properties = loadProperties();
        if(properties != null)
            return properties.getProperty("decryption_pass");
        return null;
    }
    public static String getSQLPass(){
        Properties properties = loadProperties();
        if(properties != null)
            return properties.getProperty("sql_pass");
        return null;
    }
    public static String getSQLUser(){
        Properties properties = loadProperties();
        if(properties != null)
            return properties.getProperty("sql_user");
        return null;
    }

}
