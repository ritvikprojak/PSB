package com.projak.usersyncutil.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private Properties properties = new Properties();

    private static final PropertyReader Instance = new PropertyReader();

    private PropertyReader() {
        try (InputStream ios = new FileInputStream("C:\\UserSync\\config.properties")) {
            properties.load(ios);
        } catch (IOException ex) {
            System.err.println("Error loading properties: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getProperty(final String key) {
        return getInstance().properties.getProperty(key);
    }

    public static PropertyReader getInstance() {
        return Instance;
    }
}
