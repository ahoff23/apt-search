package apt_search;

import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import org.jsoup.Jsoup;

public class AddressParser
{
    /**
     * Scrape a given URL for addresses.
     *
     * @param url The URL to scrape.
     *
     * @return A HashSet of String objects, each of which represents an
     *         address.
     */
    private static HashSet<String> getAddresses(final String url)
        throws IOException
    {
        /*
         * Store a few parameters for the GET request.
         */
        final String user_agent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
        final String referrer = "http://www.google.com";

        /*
         * Generate a HashMap of all cookies sent in the request.
         *
         * TODO: This is probably overkill. We need to update a few cookies when
         * our session is flagged as a bot, but the rest are not necessarily
         * required. Trim this list down.
         *
         * TODO: Find a way to automate retrieval of relevant cookies.
         */
        HashMap<String, String> cookies = new HashMap<String, String>();

        /*
         * These cookies seem to be constant.
         */
        cookies.put("streeteasy_site", "nyc");
        cookies.put("OX_sd", "1");
        cookies.put("last_search_tab", "rentals");
        cookies.put("ki_r", "");
        cookies.put("ki_s", "152716%3A0.0.0.0.0");
        cookies.put("D_IID", "E8DB3948-F01C-37FE-BCD0-7BF86EC0E8F5");
        cookies.put("D_UID", "A001B5E7-E22B-317A-84A6-F334B28FBE1C");

        /*
         * These don't appear in the normal cookies window, but were sent in
         * the request.
         */
        cookies.put("anon_searcher_stage", "search_made");
        cookies.put("tracked_search", "2754942");
        cookies.put("se%3Asearch%3Asales%3Astate","%7C%7C%7C%7C");
        cookies.put("se%3Asearch%3Ashared%3Astate", "109%7C2%7C%7Cfalse");

        /*
         * These may or may not change.
         */
        cookies.put("se_rs", "2754942");
        cookies.put("se_lsa", "2017-08-03+00%3A35%3A40+-0400");
        cookies.put("_dc_gtm_UA-122241-1", "1");
        cookies.put("_ga", "GA1.2.1837680605.1501348833");
        cookies.put("_gid", "GA1.2.44095435.1501348833");
        cookies.put("_gat_UA-122241-1", "1");
        cookies.put(
            "se%3Asearch%3Arentals%3Astate",
            "false%7C3500%7C4000%7C%7C");
        cookies.put(
            "__gads",
            "ID=34f06650bc9b6a8d:T=1501818271:S=ALNI_MZw2_h-FyRNd4cVRkIRJ9pU" +
            "9zV15g");
        cookies.put(
            "D_SID",
            "73.35.197.188:ikKsNcL5LNRzosYi5c22rkzK1QqaELAPUesnX+W+ByQ");
        cookies.put(
            "ki_t",
            "1501818095896%3B1501818095896%3B1501818095896%3B1%3B1");
        cookies.put(
            "_ses",
            "BAh7DUkiD3Nlc3Npb25faWQGOgZFVEkiJThlMmRiZWVjYTJhYmZiNmM4OTVjMjE0" +
            "YTE5ZGU0ZmNmBjsAVEkiEG5ld192aXNpdG9yBjsARlRJIg51c2VyX2RhdGEGOwBG" +
            "exA6EHNhbGVzX29yZGVySSIPcHJpY2VfZGVzYwY7AFQ6EnJlbnRhbHNfb3JkZXJJ" +
            "Ig9wcmljZV9kZXNjBjsAVDoQaW5fY29udHJhY3RGOg1oaWRlX21hcEY6EnNob3df" +
            "bGlzdGluZ3NGOhJtb3J0Z2FnZV90ZXJtaSM6GW1vcnRnYWdlX2Rvd25wYXltZW50" +
            "aRk6IW1vcnRnYWdlX2Rvd25wYXltZW50X2RvbGxhcnNpAlDDOhJtb3J0Z2FnZV9y" +
            "YXRlZggzLjk6E2xpc3RpbmdzX29yZGVySSIQbGlzdGVkX2Rlc2MGOwBUOhBzZWFy" +
            "Y2hfdmlld0kiDGRldGFpbHMGOwBUSSISbG9va19hbmRfZmVlbAY7AEZJIgkyMDE0" +
            "BjsAVEkiEWxhc3Rfc2VjdGlvbgY7AEZJIgxyZW50YWxzBjsAVEkiEGxhc3Rfc2Vh" +
            "cmNoBjsARmkDfgkqSSIQX2NzcmZfdG9rZW4GOwBGSSIxRlozcDlmS0xxYk00VE1t" +
            "MG1sQWpUR3piZ0lIZHVTYjVHZHNZdUgrRTIvMD0GOwBGSSIIcGlzBjsARmkG--df" +
            "d802cbbfd2db1033c9ae33336f38fecba5ebd1");

        /*
         * These definitely change, but I'm not sure if it's relevant.
         */
        cookies.put("_se_t", "ef23bb0f-0f88-45fe-9b4a-6801558eb11a");
        cookies.put("D_ZID", "C13DE44F-7E55-30DC-9F99-E1C75D13944A");

        /*
         * These cookies definitely need to be updated.
         */
        cookies.put("D_ZUID", "A5411ED3-DD07-338D-A82F-79D0A57762FA");
        cookies.put("D_HID", "A57FA764-9F89-3680-815C-A616267016C2");

        /*
         * Connect to the URL.
         */
        String doc = Jsoup.connect(url)
                          .timeout(0)
                          .userAgent(user_agent)
                          .cookies(cookies)
                          .get()
                          .html();

        /*
         * Split the document based on the header for each address.
         */
        String[] split_str = doc.split("streetAddress\":\"");

        /*
         * Parse for addresses. The first string is a bunch of garbage prior to
         * the first address.
         */
        HashSet<String> addresses = new HashSet<>();
        for (int i = 1; i < split_str.length; ++i)
        {
            addresses.add(
                split_str[i].substring(0, split_str[i].indexOf("\"")));
        }

        /*
         * Print the number of addresses for debugging purposes.
         */
        System.out.println(addresses.size());

        return addresses;
    }

    /**
     * Parse a URL from configurable parameters.
     *
     * @param neighborhood The neighborhood to search.
     * @param min_price The minimum monthly rent.
     * @param max_price The maximum monthly rent.
     * @param num_beds The number of bedrooms requested.
     * @param page_num The current page number in the search.
     *
     * @return A URL string based on the chosen parameters.
     */
    private static String generateUrl(final String neighborhood,
                                      final int min_price,
                                      final int max_price,
                                      final int num_beds,
                                      final int page_num)
    {
        return "http://streeteasy.com/for-rent/" + neighborhood + "/price:" +
                     min_price + "-" + max_price + "%7Cbeds:" + num_beds +
                     "?page=" + page_num;
    }

    /**
     * Get a list of addresses based on configurable parameters.
     *
     * @param neighborhood The neighborhood to search.
     * @param min_price The minimum monthly rent.
     * @param max_price The maximum monthly rent.
     * @param num_beds The number of bedrooms requested.
     *
     * @return A HashSet of addresses that fit the chosen parameters.
     */
    public static HashSet<String> getAddresses(final String neighborhood,
                                               final int min_price,
                                               final int max_price,
                                               final int num_beds)
    {
        /*
         * Iterate up to 5 pages. If a failed load leadss to an IOException, we
         * just break early.
         */
        HashSet<String> addresses = new HashSet<>();
        for (int i = 1; i <= 5; ++i)
        {
            try
            {
                addresses.addAll(
                    getAddresses(generateUrl(neighborhood,
                                             min_price,
                                             max_price,
                                             num_beds,
                                             i)));
            }
            catch (IOException e)
            {
                break;
            }
        }

        return addresses;
    }
}