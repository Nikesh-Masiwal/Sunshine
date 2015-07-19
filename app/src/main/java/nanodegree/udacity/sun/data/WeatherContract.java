package nanodegree.udacity.sun.data;

import android.provider.BaseColumns;

import android.text.format.Time;

/**
 * Created by Nikesh-MBP on 19/07/15.
 */
public class WeatherContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public WeatherContract() {}


    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }



    /* Inner class that defines the table contents */
    public static abstract class WeatherEntry implements BaseColumns {

        //Table name for weather entry
        public static final String TABLE_NAME = "weather";

        //Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as long in milliseconds since epoch
        public static final String COLUMN_DATE = "date";

        //Weather id as returned by API,used to identify the icon to be used.
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //Short description for weather entry. Provided by API
        public static final String COLUMN_SHORT_DESCRIPTION = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP  = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure is stored as a long/int for hPA
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind_speed";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";


    }

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class LocationEntry implements BaseColumns {

        //Table name for location entry
        public static final String TABLE_NAME = "location";

        //location setting will be sent to openweathermap
        // as location query
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        //Human readable location string , provided by API
        public static final String COLUMN_CITY_NAME = "city_name";

        //We store Lat-Long for location
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";


    }



}
