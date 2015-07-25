package nanodegree.udacity.sun.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nikesh-MBP on 19/07/15.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "weather.db";

    private static final String INTEGER_NOT_NULL_TYPE = " INTEGER NOT NULL";
    private static final String TEXT_TYPE = " TEXT NOT NULL";
    private static final String REAL_NOT_NULL_TYPE = " REAL NOT NULL";


    private static final String COMMA_SEP = ",";


    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE "+ WeatherContract.WeatherEntry.TABLE_NAME + " (" +


                WeatherContract.WeatherEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+

                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + INTEGER_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_DATE + INTEGER_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + INTEGER_NOT_NULL_TYPE + COMMA_SEP +

                WeatherContract.WeatherEntry.COLUMN_SHORT_DESCRIPTION + TEXT_TYPE + COMMA_SEP +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + REAL_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + REAL_NOT_NULL_TYPE + COMMA_SEP +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY +REAL_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE +REAL_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED +REAL_NOT_NULL_TYPE +COMMA_SEP +
                WeatherContract.WeatherEntry.COLUMN_DEGREES +REAL_NOT_NULL_TYPE + COMMA_SEP +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                        WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "+ WeatherContract.LocationEntry.TABLE_NAME + " (" +

                WeatherContract.LocationEntry._ID+" INTEGER PRIMARY KEY,"+
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL," +
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + TEXT_TYPE + COMMA_SEP +
                WeatherContract.LocationEntry.COLUMN_COORD_LAT + REAL_NOT_NULL_TYPE + COMMA_SEP +
                WeatherContract.LocationEntry.COLUMN_COORD_LONG +REAL_NOT_NULL_TYPE  +

                ");";


                db.execSQL(SQL_CREATE_WEATHER_TABLE);
               db.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);

    }
}
