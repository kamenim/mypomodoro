package org.mypomodoro.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date utility class
 *
 * @author Phil Karoo
 */
public class DateUtil {

    public static final Locale US_LOCALE = new Locale("en", "US");
    private static Locale locale = US_LOCALE;
    private static final String US_datePattern = "MMM dd yyyy";
    private static final String EN_timePattern = "hh:mm a"; // AM/PM

    public DateUtil(Locale locale) {
        this.locale = locale;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String getFormatedDate(Date date) {
        String pattern = locale.equals(US_LOCALE) ? US_datePattern : "dd MMM yyyy";
        return getFormatedDate(date, pattern);
    }

    public static String getFormatedDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern, locale).format(date);
    }

    public static String getFormatedTime(Date date) {
        String pattern = locale.getLanguage().equals("en") ? EN_timePattern : "HH:mm";
        return getFormatedTime(date, pattern);

    }

    public static String getFormatedTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern, locale).format(date);

    }

    public static boolean isUSLocale() {
        return locale.equals(US_LOCALE);
    }
}
