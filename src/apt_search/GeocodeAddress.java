package apt_search;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class that converts an address to a google maps geocoded latitude and
 * longitude.
 */
public class GeocodeAddress
{
    /**
     * A geocoded address as latitude and longitude.
     */
    public static class Location
    {
        /**
         * The address' latitude.
         */
        public float lat;

        /**
         * The address' longitude.
         */
        public float lng;
    }

    /**
     * A class to hold the JSON output of the response message.
     */
    private static class JsonData
    {
        /**
         * Maintains the results for a single address.
         */
        private static class Result
        {
            /**
             * Maintains information about components that make up the address.
             */
            private static class AddressComponent
            {
                /**
                 * The full name of the component.
                 */
                String long_name;

                /**
                 * An abbreviated form of the component's name.
                 */
                String short_name;

                /**
                 * A list of types that describe the component.
                 */
                ArrayList<String> types;
            }

            /**
             * Maintains information relating to the geometry of the address.
             */
            private static class Geometry
            {
                /**
                 * Describes a recommended frame for a search.
                 */
                private static class Bounds
                {
                    /**
                     * The northeast corner of the recommended frame.
                     */
                    Location northeast;

                    /**
                     * The southwest corner of the recommended frame.
                     */
                    Location southwest;
                }

                /**
                 * The bounds of the search.
                 */
                Bounds bounds;

                /**
                 * The location of the address.
                 */
                Location location;

                /**
                 * A string describing location type information pertaining to
                 * the address.
                 */
                String location_type;

                /**
                 * The viewport frame recommended for viewing the location.
                 */
                Bounds viewport;
            }

            /**
             * A list of all components that make up the address.
             */
            ArrayList<AddressComponent> address_components;

            /**
             * The full text of the address name.
             */
            String formatted_address;

            /**
             * The address geometry.
             */
            Geometry geometry;

            /**
             * The ID of the address.
             */
            String place_id;

            /**
             * A list of types that pertain to the address.
             */
            ArrayList<String> types;  
        }

        /**
         * A list of all results.
         */
        public ArrayList<Result> results;

        /**
         * A status string.
         */
        public String status;
    }

    /**
     * Get an Geocode struct from an address.
     *
     * @param addr The address.
     *
     * @return The geocoded address as a latitude and longitude.
     */
    public static Location get_geocode(final String addr) throws IOException
    {
        /*
         * Construct the query string.
         */
        final String formatted_addr = addr.replaceAll(" ", "+");
        final String key = "AIzaSyA1CCZ5DuDQN83MpfKhproce9djH139GLg";
        final String url =
            "https://maps.googleapis.com/maps/api/geocode/json?address=" +
            formatted_addr + "&key=" + key;

        /*
         * Query the google maps API.
         */
        InputStream stream = new URL(url).openStream();
        Reader reader = new InputStreamReader(stream, "UTF-8");
        JsonData data = new Gson().fromJson(reader, JsonData.class);

        /*
         * If we have no results, return a bad Location.
         */
        if (data.results.size() == 0)
        {
            Location location = new Location();
            location.lat = 0.0f;
            location.lng = 0.0f;
            return location;
        }

        /*
         * Return the latitude and longitude location.
         */
        return data.results.get(0).geometry.location;
    }
}