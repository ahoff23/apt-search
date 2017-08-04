package apt_search;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

public class MapFilter
{
    private class JsonData
    {
        private class Distance
        {
            int value;
            String text;
        }

        private class Duration
        {
            int value;
            String text;
        }

        private class Element
        {
            String status;
            Distance duration;
            Duration distance;
        }

        private class Row
        {
            ArrayList<Element> elements;
        }

        String status;
        ArrayList<String> origin_addresses;
        ArrayList<String> destination_addresses;
        ArrayList<Row> rows;
    }

    /**
     * Constructor.
     */
    public MapFilter()
    {
        addresses = new HashSet<>();
    }

    /**
     * Add a set of addresses to the member set of addresses.
     *
     * @param str An address to add.
     */
    public void add(final String str)
    {
        addresses.add(str);
    }

    /**
     * Filter all addresses against a target address and travel time.
     *
     * @param destination The destination address.
     * @param max_travel_time The max travel time to filter against. Units in
     *        minutes.
     */
    public void filterAddresses(final String destination,
                                final int max_travel_time) throws IOException
    {
        /*
         * Store a user agent for the HTTP request.
         */
        final String user_agent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

        /*
         * Key to use for the google maps API.
         */
        final String key = "";

        /*
         * Iterate over each address.
         */
        for(Iterator<String> itr = addresses.iterator(); itr.hasNext();)
        {
            String address = itr.next();

            /*
             * Format the addresses before searching.
             */
            final String formatted_address = address.replaceAll(" ", "+");
            final String formatted_destination =
                destination.replaceAll(" ", "+");

            final String url =
                "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins=" + formatted_address +
                "&destinations=" + formatted_destination +
                "&key=" + key;

            /*
             * Get the travel time between the address and
             * the target. If the travel time exceeds the maximum travel time,
             * remove the address.
             */
            InputStream stream = new URL(url).openStream();
            Reader reader = new InputStreamReader(stream, "UTF-8");
            JsonData data = new Gson().fromJson(reader, JsonData.class);
            if (data.rows.get(0).elements.get(0).duration.value / 60 >
                max_travel_time)
            {
                itr.remove();
            }
        }
    }

    /**
     * Get all addresses.
     *
     * @return The current set of addresses.
     */
    public HashSet<String> getAddresses()
    {
        return new HashSet<String>(addresses);
    }

    private HashSet<String> addresses;
}