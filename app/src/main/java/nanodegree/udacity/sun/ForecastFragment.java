package nanodegree.udacity.sun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> weatherAdapter;
    String preferred_unit;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflating fragment layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        //Defining ArrayAdapter
        weatherAdapter = new ArrayAdapter<String>(
                //Context of Fragment
                getActivity(),
                //Reference to List Layout
                R.layout.list_item_forecast,
                //Reference to TextView we defined in list_item layout
                R.id.list_item_forecast_textview,
                //Fake Array we defined above
                new ArrayList<String>());


        //Defining List View, referencing with findViewByID
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        //setAdapter is a method of ListView to Adapter List to screen
        listView.setAdapter(weatherAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               String currentForecast = weatherAdapter.getItem(position);

                Intent startDetailActivity = new Intent(getActivity(),DetailActivity.class);
                startDetailActivity.putExtra("WEATHER_DETAIL",currentForecast);
                startActivity(startDetailActivity);


            }
        });



        return rootView;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            updateWeather();

            return true;
        }

        if (id == R.id.action_maplocation) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = prefs.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            Uri locateUri = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW, locateUri);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather(){

        //FetchWeatherTask weatherTask = new FetchWeatherTask();


        nanodegree.udacity.sun.FetchWeatherTask weatherTask =
                new nanodegree.udacity.sun.FetchWeatherTask(getActivity(),weatherAdapter);

//            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//            String defaultValue = getResources().getString(R.string.pref_location_default);
//            String locationFromPref = sharedPref.getString("location", defaultValue);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        SharedPreferences pref_units = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferred_unit = pref_units.getString(getString(R.string.pref_units_key),
                getString(R.string.pref_units_default));


        weatherTask.execute(location);



    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            roundedHigh = getTempInPreferredUnits(roundedHigh);
            roundedLow = getTempInPreferredUnits(roundedLow);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        private long getTempInPreferredUnits(long currentTemp){

            Log.d("Units","Checking");

            if (preferred_unit.equals("metric")){
                Log.d("Units","Metric");
                return currentTemp;

            }else{
                Log.d("Units","Imperial");
                long farenheit_temp = (long)(currentTemp*1.8+32);

                return farenheit_temp;
            }



        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            /*for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }*/
            return resultStrs;

        }




        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            if (params.length == 0){
                return null;
            }


            String POSTAL_CODE = params[0];
            String FORMAT_MODE = "json";
            String UNITS = "metric";
            int NUM_OF_DAYS_WEATHER = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, POSTAL_CODE)
                        .appendQueryParameter(FORMAT_PARAM,FORMAT_MODE)
                        .appendQueryParameter(UNITS_PARAM,UNITS)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(NUM_OF_DAYS_WEATHER))
                        .build();


                URL url = new URL(builtUri.toString());




                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();


                /*Log.v(LOG_TAG, "JSONDATA FROM SERVER "+ forecastJsonStr);

                JSONObject weatherJSONObj = new JSONObject(forecastJsonStr);

                JSONArray listArray = weatherJSONObj.getJSONArray("list");
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject c = listArray.getJSONObject(i);

                    JSONObject temp = c.getJSONObject("temp");

                    String max_temp = temp.getString("max");



                    Log.i(LOG_TAG, "MAX "+ max_temp);


                }*/



            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, NUM_OF_DAYS_WEATHER);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }



            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            if (result != null) {
                weatherAdapter.clear();

                weatherAdapter.addAll(result);



            }



        }

    }


}
