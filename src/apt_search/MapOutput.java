package apt_search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

/**
 * Class that takes a set of addresses and creates a google maps
 * HTML page.
 */
public class MapOutput
{
    /**
     * Create the output file.
     *
     * @param addrs the addresses to print.
     */
    public static void create_output(Set<AddressParser.UrlAddress> addrs)
        throws IOException
    {
        /* 
         * Create the initial boilerplate string.
         */
        String html = "<!DOCTYPE html>\n" +
                       "<html>\n" +
                       "<head>\n" +
                       "<style>\n" +
                       "#map {\n" +
                        "height: 800px;\n" +
                        "width: 800px;\n" +
                       "}\n" +
                       "</style>\n" +
                       "</head>\n" +
                       "<body>\n" +
                       "<div id=\"map\"></div>\n" +
                       "<script>\n" +
                       "function initMap() {\n" +
                        "var nyc = {lat: 40.725910, lng: -73.992788};\n" +
                        "var map = new google.maps.Map(document.getElementById('map'), {\n" +
                        "zoom: 14,\n" +
                        "center: nyc\n" +
                        "});\n";

        /*
         * Add a marker for each address.
         */
        int index = 0;
        for (AddressParser.UrlAddress addr : addrs)
        {
            final GeocodeAddress.Location loc =
                GeocodeAddress.get_geocode(addr.address);
            html += "var loc" + index + 
                    " = {lat: " + loc.lat + ", lng: " + loc.lng +
                    "};";
            html += "var marker" + index + " = new google.maps.Marker({\n" +
                    "position: loc" + index + ",\n" +
                    "url: \'" + addr.url + "\',\n" +
                    "map: map\n" +
                    "});\n";

            html +=
                "google.maps.event.addListener(marker" + index +
                ", 'click', function() { window.open(marker" +
                index + ".url)});";

            ++index;
        }

        /*
         * Append boilerplate after the markers.
         */
        html += "}\n" +
                "</script>\n" +
                "<script async defer\n" +
                "src=\"https://maps.googleapis.com/maps/api/js?key=&callback=initMap\">\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";

        /*
         * Create a file and print the output.
         */
        PrintWriter writer = new PrintWriter("../output", "UTF-8");
        writer.println(html);
        writer.close();
    }
}