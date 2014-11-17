package mn.aug.restfulandroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Antoine on 16/11/2014.
 */
public class DateHelper {

    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     *
     * @param yourFormat String vous passez le format que vous voulez "yyyy-MM-dd HH:mm" par exemple
     * @param yourDate en théorie à votre format ex: "2014-12-25"
     * @return String votre date à votre format
     * @throws ParseException Si jamais votre format ou votre date n'est pas bonne, bim grosse exception !
     */
    public static String getDateToYourFormat(String yourFormat, String yourDate) throws ParseException{
        SimpleDateFormat theFormat = new SimpleDateFormat(yourFormat);

        String retour = null;
        Date parsedTimeStamp = theFormat.parse(yourDate);
        retour = String.valueOf(theFormat.format(parsedTimeStamp.getTime()));
        return retour;
    }

    /**
     *
     * @param yourFormat String vous passez le format que vous voulez "yyyy-MM-dd HH:mm" par exemple
     * @param yourTime ex: 1785624688
     * @return String votre date à votre format
     * @throws ParseException Si jamais votre format ou votre date n'est pas bonne, bim grosse exception !
     */
    public static String getDateToYourFormat(String yourFormat, long yourTime) throws ParseException{
        SimpleDateFormat theFormat = new SimpleDateFormat(yourFormat);
        return theFormat.format(yourTime);
    }

    /**
     *
     * @param dateTime String au format "yyyy-MM-dd HH:mm"
     * @return String "yyyy-MM-dd"
     * @throws ParseException format attendu non respecté
     */
    public static String getDateTimeFromDateTime(String dateTime) throws ParseException{
        String retour = null;
        Date parsedTimeStamp = dateTimeFormat.parse(dateTime);
        retour = String.valueOf(dateTimeFormat.format(parsedTimeStamp.getTime()));
        return retour;
    }


    /**
     *
     * @param date String au format "yyyy-MM-dd" ou même "yyyy-MM-dd HH:mm"
     * @return String "yyyy-MM-dd"
     * @throws ParseException format attendu non respecté
     */
    public static String getDateFromDate(String date) throws ParseException{
        String retour = null;
        Date parsedTimeStamp = dateFormat.parse(date);
        retour = String.valueOf(dateFormat.format(parsedTimeStamp.getTime()));
        return retour;
    }

    /**
     *
     * @param dateTime String au format "yyyy-MM-dd HH:mm"
     * @return String "yyyy-MM-dd"
     * @throws ParseException format attendu non respecté
     */
    public static String getTimeFromDateTime(String dateTime) throws ParseException{
        String retour = null;
        Date parsedTimeStamp = dateTimeFormat.parse(dateTime);
        retour = String.valueOf(timeFormat.format(parsedTimeStamp.getTime()));
        return retour;
    }
    /**
     *
     * @param time String au format "yyyy-MM-dd HH:mm"
     * @return String "yyyy-MM-dd"
     * @throws ParseException format attendu non respecté
     */
    public static String getTimeFromTime(String time) throws ParseException{
        String retour = null;
        Date parsedTimeStamp = timeFormat.parse(time);
        retour = String.valueOf(timeFormat.format(parsedTimeStamp.getTime()));
        return retour;
    }

    public static String getDateFromDate(long time) {
        return dateFormat.format(time);
    }
}