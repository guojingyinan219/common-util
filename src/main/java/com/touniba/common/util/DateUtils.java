package com.touniba.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils
 */
public class DateUtils {

    /**
     * Get real year
     *
     * @param date
     * @return
     */
    public static int getRealYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static void main(String[] args) {
        System.out.println(formatDate(new Date(1493351402116L), "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /**
     * Get the current date string with format
     *
     * @param format
     * @return
     */
    public static String currentDate(String format) {
        return formatDate(new Date(), format);
    }

    /**
     * Format the date to string
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * Parse the date string to date
     *
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(dateStr);
    }

    /**
     * Transform the date string between with two formats
     *
     * @param dateStr
     * @param srcFormat
     * @param destFormat
     * @return
     */
    public static String transform(String dateStr, String srcFormat, String destFormat) {
        try {
            Date date = parseDate(dateStr, srcFormat);
            return formatDate(date, destFormat);
        } catch (ParseException e) {
        }
        return null;
    }
}
