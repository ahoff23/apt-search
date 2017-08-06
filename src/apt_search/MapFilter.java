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
    /**
     * The JSON response we expect from the Google Maps API.
     */
    private class JsonData
    {
        /**
         * The distance element for a given search. We ignore this for now.
         */
        private class Distance
        {
            /**
             * The travel distance.
             */
            int value;

            /**
             * The travel distance as a readable string.
             */
            String text;
        }

        /**
         * The travel duration for a given search.
         */
        private class Duration
        {
            /**
             * The travel time in seconds.
             */
            int value;

            /**
             * The travel time as a readable string.
             */
            String text;
        }

        /**
         * An element contains information regaring a single point-to-point
         * travel search i.e. a single origin and a single destination.
         */
        private class Element
        {
            /**
             * The query's status.
             */
            String status;

            /**
             * The query's duration.
             */
            Distance duration;

            /**
             * The query's distance.
             */
            Duration distance;
        }

        /**
         * A row maintains all elements starting from a  single origin.
         */
        private class Row
        {
            ArrayList<Element> elements;
        }

        /**
         * The status of the query as a whole.
         */
        String status;

        /**
         * An echoed list of origins.
         */
        ArrayList<String> origin_addresses;

        /**
         * An echoed list of destinations.
         */
        ArrayList<String> destination_addresses;

        /**
         * All rows returned by the search.
         */
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
         * Iterate over each address and add it to the origin addresses.
         */
        String origins = "";
        for(Iterator<String> itr = addresses.iterator(); itr.hasNext();)
        {
            String address = itr.next();

            /*
             * Format the addresses before appending.
             */
            final String formatted_address = address.replaceAll(" ", "+") +
                "+New+York+,+NY";

            if (origins != "")
                origins += "|";
            origins += formatted_address;
        }

        /*
         * Create the desination string, then create the entire url.
         */
        final String formatted_destination = destination.replaceAll(" ", "+");
        final String url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "origins=" + origins +
            "&destinations=" + formatted_destination +
            "&key=" + key;

        /*
         * Query the google maps API.
         */
        InputStream stream = new URL(url).openStream();
        Reader reader = new InputStreamReader(stream, "UTF-8");
        JsonData data = new Gson().fromJson(reader, JsonData.class);

        /*
         * Validate that our response row count matches the number of origins
         * we sent.
         */
        ArrayList<JsonData.Row> responses = data.rows;
        if (responses.size() != addresses.size())
            throw new IOException("Number of origins does not match response.");

        /*
         * Get the travel time between the origin addresses and the target.
         * If the travel time exceeds the maximum travel time, remove the
         * origin address.
         */
        int row_idx = 0;
        for (Iterator<String> itr = addresses.iterator(); itr.hasNext();)
        {
            itr.next();

            final JsonData.Element element =
                responses.get(row_idx).elements.get(0);

            /*
             * Multiply by 60 to compare both as seconds.
             */
            if (element.duration.value > max_travel_time * 60)
                itr.remove();

            ++row_idx;
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