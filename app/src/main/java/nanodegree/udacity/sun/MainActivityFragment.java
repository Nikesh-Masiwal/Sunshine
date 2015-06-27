package nanodegree.udacity.sun;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflating fragment layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // String array to push it to Adapter to populate fake data
        String[] weatherData = {"Today - Sunny 85/32","Tomorrow - Cloudly 87/30","Friday - Light Rain 85/29",
                "Saturday - Hot 85/34","Sunday - Rain 85/26","Monday - Sunny 85/32","Tuesday - Drizzle 85/30"};


        //Converting to ArrayList
        List<String> weekForecast = new ArrayList<>( Arrays.asList(weatherData));

        //Defining ArrayAdapter
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<>(
                //Context of Fragment
                getActivity(),
                //Reference to List Layout
                R.layout.list_item_forecast,
                //Reference to TextView we defined in list_item layout
                R.id.list_item_forecast_textview,
                //Fake Array we defined above
                weekForecast);



        //Defining List View, referencing with findViewByID
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        //setAdapter is a method of ListView to Adapter List to screen
        listView.setAdapter(weatherAdapter);




        return rootView;
    }
}
