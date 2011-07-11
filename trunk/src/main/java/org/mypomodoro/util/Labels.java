package org.mypomodoro.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Labels bundle
 * 
 * @author p.caroux
 */
public class Labels {

    private static final String BUNDLE_NAME = "org.mypomodoro.labels.mypomodoro";
    private static ResourceBundle resource_bundle;

    public Labels(Locale locale) {
        resource_bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static String getString(String key) {
        try {
            return resource_bundle.getString(key);
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object... params) {
        try {
            return MessageFormat.format(resource_bundle.getString(key), params);
        }
        catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}