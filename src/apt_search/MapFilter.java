package apt_search;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.NullPointerException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapFilter
{
    /**
     * The JSON response we expect from the Google Maps API.
     */
    private static class JsonData
    {
        /**
         * The distance element for a given search. We ignore this for now.
         */
        private static class Distance
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
        private static class Duration
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
        private static class Element
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
         * A row maintains all elements starting from a single origin.
         */
        private static class Row
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
     * Update a HashMap whose keys are addresses by setting the corresponding
     * values to the address' travel times.
     *
     * @param addresses A HashMap of addresses to query.
     * @param destination The destination address.
     */
    private static void getTravelTimes(HashMap<Address, Integer> addresses,
                                       final String destination)
        throws IOException
    {
        /*
         * Store a user agent for the HTTP request.
         */
        final String user_agent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

        /*
         * Iterate over each address and add it to the origin addresses.
         */
        String origins = "";
        for (Address entry : addresses.keySet())
        {
            final String str_addr = entry.address;

            /*
             * Format the addresses before appending.
             */
            final String formatted_address = str_addr.replaceAll(" ", "+");
            if (origins != "")
                origins += "|";
            origins += formatted_address;
        }

        /*
         * Create the desination string, then create the entire url with an
         * empty key.
         */
        final String key = "AIzaSyAeEM8OUkWWMq3qTlkJkWVtDJIf4DvZgig";
        final String formatted_destination = destination.replaceAll(" ", "+");
        final String url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "origins=" + origins +
            "&destinations=" + formatted_destination +
            "&mode=transit" +
            "&transit_mode=subway" +
            "&transit_routing_preference=fewer_transfers" +
            "&key=" + key;

        /*
         * Query the google maps API.
         */
        InputStream stream = new URL(url).openStream();
        Reader reader = new InputStreamReader(stream, "UTF-8");
        JsonData data = new Gson().fromJson(reader, JsonData.class);

        if (addresses.size() != data.rows.size())
        {
            System.out.println("ERROR - query size does not match result size.");
            System.out.println("Query size: " + addresses.size());
            System.out.println("Results size: " + data.rows.size());
            return;
        }

        int index = 0;
        for (Address entry : addresses.keySet())
        {
            if (data.rows.size() <= index)
            {
                System.out.println("ERROR - Bad result 1.");
                continue;
            }
            else if (data.rows.get(index).elements.size() == 0)
            {
                System.out.println("ERROR - Bad result 2.");
                continue;
            }

            try
            {
                addresses.put(
                   entry, data.rows.get(index).elements.get(0).duration.value);
            }
            catch (NullPointerException e)
            {}

            ++index;
        }
    }

    /**
     * Filter all addresses against a target address and travel time.
     *
     * @param addresses A Set of possible addresses. The class must support
     *        a function to return a String address.
     * @param destination The destination address.
     * @param max_travel_time The max travel time to filter against. Units in
     *        minutes.
     */
    public static void filterAddresses(
        Set<? extends Address> addresses,
        final String destination,
        final int max_travel_time) throws IOException
    {
        /*
         * Iterate over addresses and process in batches.
         */
        ArrayList<HashMap<Address, Integer>> batches =
            new ArrayList<HashMap<Address, Integer>>();
        int addr_index = 0;
        final int batch_size = 20;
        for(Address addr : addresses)
        {
            if (addr_index % batch_size == 0)
                batches.add(new HashMap<Address, Integer>());
            batches.get(batches.size() - 1).put(addr, 0);

            ++addr_index;
        }

        /*
         * For each batch, get the corresponding travel times, then remove
         * the address if the travel is too long.
         */
        for (HashMap<Address, Integer> batch : batches)
        {
            /*
             * Fill in the travel times.
             */
            getTravelTimes(batch, destination);

            /*
             * Filter addresses as necessary.
             */
            for (Address entry : batch.keySet())
            {
                final Integer travel_time = batch.get(entry);
                System.out.println(entry.address + ": " + travel_time / 60);

                /*
                 * Multiply by 60 to compare both as seconds.
                 */
                if (travel_time > max_travel_time * 60)
                    addresses.remove(entry);
            }
        }
    }
}