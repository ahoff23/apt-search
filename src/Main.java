
import apt_search.MapFilter;
import apt_search.AddressParser;

import java.io.IOException;
import java.io.PrintWriter;
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

        /*
         * Create a file and print the output.
         */
        PrintWriter writer = new PrintWriter("../output", "UTF-8");
        for (AddressParser.UrlAddress addr : addrs)
            writer.println("<a href=\"" + addr.url + "\">" + addr.address + "</a><br/>");
        writer.close();
    }
}