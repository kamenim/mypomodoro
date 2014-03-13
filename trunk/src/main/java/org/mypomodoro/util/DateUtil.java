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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Date utility class
 *
 */
public class DateUtil {

    public static final Locale US_LOCALE = new Locale("en", "US");
    private static Locale locale = US_LOCALE;
    private static final String US_datePattern = "MMM dd yyyy";
    private static final String EN_timePattern = "hh:mm a"; // AM/PM

    public DateUtil(Locale locale) {
        DateUtil.locale = locale;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String getFormatedDate(Date date) {
        String pattern = locale.equals(US_LOCALE) ? US_datePattern : "dd MMM yyyy";
        return getFormatedDate(date, pattern);
    }

    public static String getFormatedDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String getFormatedTime(Date date) {
        String pattern = locale.getLanguage().equals("en") ? EN_timePattern : "HH:mm";
        return getFormatedTime(date, pattern);
    }

    public static String getFormatedTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static boolean isUSLocale() {
        return locale.equals(US_LOCALE);
    }

    /*
     * Converts a string into a date
     * 
     * @param formatedDateTime string with date and time (eg "13/05/2000 12:46")
     * @param datePattern pattern of the date (eg dd/MM/yyy)
     */
    public static Date getDate(String formatedDateTime, String datePattern) throws ParseException {
        String timePattern = locale.getLanguage().equals("en") ? EN_timePattern : "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern + " " + timePattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(formatedDateTime);
    }

    public boolean isDateToday(Date date) {
        String datePickerFormat = DateUtil.getFormatedDate(date);
        String todayFormat = DateUtil.getFormatedDate(new Date());
        return datePickerFormat.equalsIgnoreCase(todayFormat);
    }
}
