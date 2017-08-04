
import apt_search.MapFilter;
import apt_search.AddressParser;

import java.io.IOException;
import java.util.HashSet;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        /*
         * Get all addresses in Manhattan.
         */
        HashSet<String> addrs =
                AddressParser.getAddresses("manhattan", 3500, 4000, 2);

        /*
         * Pass all addresses into the filter.
         */
        MapFilter filter = new MapFilter();
        for (String addr : addrs)
            filter.add(addr);

        /*
         * Filter against the distance to a desired location.
         */
        filter.filterAddresses("100 6th Ave New York NY", 20);

        /*
         * Print the list of addresses.
         */
        HashSet<String> addresses = filter.getAddresses();
        for (String addr : addresses)
            System.out.println(addr);
    }
}