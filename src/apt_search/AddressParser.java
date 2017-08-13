package apt_search;

import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import org.jsoup.Jsoup;

public class AddressParser
{
    /**
     * Class maintaing information relevant to a single address.
     */
    public static class UrlAddress extends Address
    {
        /**
         * The URL of the listing.
         */
        public String url;

        /**
         * Function that validates if two UrlAddresses are equal.
         */
        public boolean equals(UrlAddress addr)
        {
            return url == addr.url;
        }
    }

    /**
     * Scrape a given URL for addresses.
     *
     * @param url The URL to scrape.
     *
     * @return A HashSet of UrlAddress objects.
     */
    private static HashSet<UrlAddress> getAddresses(final String url)
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
        cookies.put("last_search_tab", "rentals");
        cookies.put("ki_r", "");
        cookies.put("ki_s", "152716%3A0.0.0.0.0");
        cookies.put(
            "D_SID",
            "73.35.197.188:ikKsNcL5LNRzosYi5c22rkzK1QqaELAPUesnX+W+ByQ");
        cookies.put("_dc_gtm_UA-122241-1", "1");

        /*
         * These don't appear in the normal cookies window, but were sent in
         * the request.
         */
        cookies.put("tracked_search", "2754942");
        cookies.put("se%3Asearch%3Asales%3Astate","%7C%7C%7C%7C");
        cookies.put("se%3Asearch%3Ashared%3Astate", "109%7C2%7C%7Cfalse");

        /*
         * These may or may not change.
         */
        cookies.put("split", "%7B%22rental_hdp_03_2017%22%3A%22original%22%7D");
        cookies.put("se_login_trigger", "10");
        cookies.put("_uetsid", "_uet313d737d");
        cookies.put("_mibhv", "anon-1501953202846-6800285451_6815");
        cookies.put("se_rs", "2754942");
        cookies.put("se_lsa", "2017-08-05+13%3A19%3A14+-0400");
        cookies.put("_gat_UA-122241-1", "1");
        cookies.put(
            "se%3Asearch%3Arentals%3Astate",
            "false%7C3500%7C4000%7C%7C");

        /*
         * These definitely change, but I'm not sure if it's relevant.
         */
        cookies.put(
            "ki_t",
            "1501818095896%3B1501818095896%3B1501818095896%3B1%3B1");
        cookies.put("anon_searcher_stage", "search_made");
        cookies.put("OX_sd", "1");
        cookies.put("D_UID", "A001B5E7-E22B-317A-84A6-F334B28FBE1C");
        cookies.put("D_IID", "E8DB3948-F01C-37FE-BCD0-7BF86EC0E8F5");
        cookies.put("_se_t", "ef23bb0f-0f88-45fe-9b4a-6801558eb11a");
        cookies.put("D_ZID", "06DE0332-13FA-3849-A203-431F29B087C1");
        cookies.put(
            "__gads",
            "ID=34f06650bc9b6a8d:T=1501818271:S=ALNI_MZw2_h-FyRNd4cVRkIRJ9pU" +
            "9zV15g");
        cookies.put("_ga", "GA1.2.1837680605.1501348833");
        cookies.put("_gid", "GA1.2.44095435.1501348833");
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
         * These cookies definitely need to be updated.
         */
        cookies.put("D_ZUID", "460999DB-E41A-3467-AA18-51D83D70CC8C");
        cookies.put("D_HID", "15CADEDE-1645-3359-A72E-49D741537D90");

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
         * Split the document along rental listings.
         */
        String[] split_str = doc.split("data-gtm-listing-type=\"rental\"");

        /*
         * Get the link name and address of each listing.
         */
        HashSet<UrlAddress> addresses = new HashSet<>();
        for (int i = 1; i < split_str.length; ++i)
        {
            /*
             * Get the link URL if one exists.
             */
            UrlAddress addr = new UrlAddress();
            String[] sub_split_str_1 = split_str[i].split("href=\"");
            if (sub_split_str_1.length == 1)
                continue;
            addr.url = "https://www.streeteasy.com/" +
                sub_split_str_1[1].substring(
                    0, sub_split_str_1[1].indexOf("\""));

            /*
             * If an address name exists, add it to the Address instance and
             * add the completed instance to the HashSet. Incomplete Addresses
             * should be skipped.
             */
            String[] sub_split_str_2 = sub_split_str_1[1].split("img alt=\"");
            if (sub_split_str_2.length == 1)
                continue;
            addr.address = sub_split_str_2[1].substring(
                0, sub_split_str_2[1].indexOf("\""));
            addresses.add(addr);
        }

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
    public static HashSet<UrlAddress> getAddresses(final String neighborhood,
                                                   final int min_price,
                                                   final int max_price,
                                                   final int num_beds)
        throws IOException
    {
        /*
         * Iterate up to 5 pages. If a failed load leadss to a bad URL, we
         * just break early.
         */
        HashSet<UrlAddress> addresses = new HashSet<>();
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
            catch (org.jsoup.HttpStatusException e)
            {
                break;
            }
        }

        return addresses;
    }
}