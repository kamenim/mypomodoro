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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeConstants;

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

    // TODO check time zone issue with export dates
    public static String getFormatedDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        //sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String getFormatedTime(Date date) {
        String pattern = locale.getLanguage().equals("en") ? EN_timePattern : "HH:mm";
        return getFormatedTime(date, pattern);
    }

    public static String getFormatedTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        //sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
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
        //sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(formatedDateTime);
    }

    /*
     * Check if a date is today
     * 
     * @param date
     */
    public static boolean isDateToday(Date date) {
        DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();
        return dateComparator.compare(date, new Date()) == 0;
    }

    /*
     * Check if a date is in the past
     * 
     * @param date
     */
    public static boolean inPast(Date date) {
        DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();
        return dateComparator.compare(date, new Date()) == -1;
    }

    /*
     * Check if a date is in the future
     * 
     * @param date
     */
    public static boolean inFuture(Date date) {
        DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();
        return dateComparator.compare(date, new Date()) == 1;
    }

    /*
     * Check if a date1 is sooner than date2
     * 
     * @param date1
     * @param date2
     */
    public static boolean isSooner(Date date1, Date date2) {
        DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();
        return dateComparator.compare(date1, date2) == -1;
    }
    
    /*
     * Check if a date1 is sooner than date2
     * 
     * @param date1
     * @param date2
     */
    public static boolean isEquals(Date date1, Date date2) {
        DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();
        return dateComparator.compare(date1, date2) == 0;
    }

    /**
     * Returns an ordered list of days of month between two dates
     *
     * @param dateStart
     * @param dateEnd
     * @return array list of days of months
     */
    /*public static ArrayList<Integer> getDaysOfMonth(Date dateStart, Date dateEnd) {
        DateTime start = new DateTime(dateStart.getTime());
        DateTime end = new DateTime(dateEnd.getTime());
        ArrayList<Integer> days = new ArrayList<Integer>();
        while (start.isBefore(end) || start.isEqual(end)) {
            days.add(start.dayOfMonth().get());
            start = start.plusDays(1);
        }
        return days;
    }*/

    /**
     * Returns an ordered list of dates of month between two dates minus
     * exclusions
     *
     * @param dateStart
     * @param dateEnd
     * @param excludeSaturdays
     * @param excludeSundays
     * @param excludeDates
     * @return array list of dates of months excluding some exceptions
     */
    public static ArrayList<Date> getDatesWithExclusions(Date dateStart, Date dateEnd, boolean excludeSaturdays, boolean excludeSundays, ArrayList<Date> excludeDates) {
        DateTime start = new DateTime(dateStart.getTime());
        DateTime end = new DateTime(dateEnd.getTime());
        ArrayList<Date> dates = new ArrayList<Date>();
        while (start.isBefore(end) || start.isEqual(end)) {
            if (!isExcluded(start, excludeSaturdays, excludeSundays, excludeDates)) {
                dates.add(start.toDate());
            }
            start = start.plusDays(1);
        }
        return dates;
    }

    /**
     * Check if a date is part of the exclusion list
     *
     * @param dateTime
     * @param excludeSaturdays
     * @param excludeSundays
     * @param excludeDates
     * @return true if excluded
     */
    private static boolean isExcluded(DateTime dateTime, boolean excludeSaturdays, boolean excludeSundays, ArrayList<Date> excludeDates) {
        boolean isExcluded = false;
        if (excludeSaturdays && dateTime.getDayOfWeek() == DateTimeConstants.SATURDAY) { // excluding saturdays
            isExcluded = true;
        } else if (excludeSundays && dateTime.getDayOfWeek() == DateTimeConstants.SUNDAY) { // excluding sundays
            isExcluded = true;
        } else {
            for (Date excludeDate : excludeDates) {
                if (isSameDay(excludeDate, dateTime.toDate())) {
                    isExcluded = true;
                    break;
                }
            }
        }
        return isExcluded;
    }

    /**
     * Returns the day of month of a date
     *
     * @param date
     * @return day of month
     */
    public static int convertToDayOfMonth(Date date) {
        return new DateTime(date.getTime()).dayOfMonth().get();
    }

    /**
     * Compares two dates
     *
     * @param date1
     * @param date2
     * @return true is dates are equal
     */
    public static boolean isSameDay(Date date1, Date date2) {
        DateTime dateTime1 = new DateTime(date1);
        DateTime dateTime2 = new DateTime(date2);
        return dateTime1.withTimeAtStartOfDay().isEqual(dateTime2.withTimeAtStartOfDay());
    }

    /**
     * Returns the date at the start of the day
     *
     * @param date
     * @return date at start of day
     */
    public static Date getDateAtStartOfDay(Date date) {
        return new DateTime(date).withTimeAtStartOfDay().toDate();
    }

    /**
     * Returns the date at midnight
     *
     * @param date
     * @return date at midnight
     */
    public static Date getDateAtMidnight(Date date) {
        return new DateTime(date).plusDays(1).withTimeAtStartOfDay().toDate();
    }
}
