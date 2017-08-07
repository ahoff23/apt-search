import apt_search.AddressParser;
import apt_search.MapFilter;
import apt_search.MapOutput;

import java.io.IOException;
import java.util.HashSet;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        /*
         * Get all addresses in Manhattan.
         */
        HashSet<AddressParser.UrlAddress> addrs =
                AddressParser.getAddresses("manhattan", 3500, 4000, 2);

        /*
         * Filter against the distance to a desired location.
         */
        MapFilter.filterAddresses(addrs, "100 6th Ave New York NY", 20);

        MapOutput.create_output(addrs);
    }
}