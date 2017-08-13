import apt_search.AddressParser;
import apt_search.MapFilter;
import apt_search.MapOutput;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Scanner scan = new Scanner(System.in);

        /*
         * Get the neighborhood.
         */
        System.out.print("Enter neighborhood: ");
        final String neighborhood = scan.nextLine();

        /*
         * Get the destination.
         */
        System.out.print("Enter destination: ");
        final String destination = scan.nextLine();

        /*
         * Get the destination travel time in minutes.
         */
        System.out.print("Enter max travel time: ");
        final int travel_time = scan.nextInt();

        /*
         * Get the min and max price.
         */
        System.out.print("Enter min rent: ");
        final int min_price = scan.nextInt();
        System.out.print("Enter max rent: ");
        final int max_price = scan.nextInt();

        /*
         * Get the number of bedrooms.
         */
        System.out.print("Enter the number of bedrooms: ");
        final int num_bedrooms = scan.nextInt();

        /*
         * Get all addresses in Manhattan.
         */
        HashSet<AddressParser.UrlAddress> addrs = AddressParser.getAddresses(
            neighborhood, min_price, max_price, num_bedrooms);

        /*
         * Filter against the distance to a desired location.
         */
        MapFilter.filterAddresses(addrs, destination, travel_time);

        MapOutput.create_output(addrs);
    }
}