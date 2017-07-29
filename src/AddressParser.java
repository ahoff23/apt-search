import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;

public class AddressParser
{
    /**
     * Scrape a given URL for addresses.
     *
     * @param url The URL to scrape.
     *
     * @return An ArrayList of String objects, each of which represents an
     *         address.
     */
    private static ArrayList<String> getAddresses(final String url)
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
         * TODO: This is probably overkill. We need to update D_ZUID when our
         * session is flagged as a bot, but the rest are not necessarily
         * required. Trim this list down.
         *
         * TODO: Find a way to automate retrieval of D_ZUID.
         */
        HashMap<String, String> cookies = new HashMap<String, String>();
        cookies.put("streeteasy_site", "nyc");
        cookies.put("_se_t", "fc1ca76a-c389-4687-81b0-8a15ffbd0605");
        cookies.put("ki_s", "152716%3A0.0.0.0.0");
        cookies.put("se%3Asearch%3Asales%3Astate","%7C%7C%7C%7C");
        cookies.put("se%3Asearch%3Ashared%3Astate", "109%7C2%7C%7Cfalse");
        cookies.put("anon_searcher_stage", "search_made");
        cookies.put("tracked_search", "2754942");
        cookies.put("last_search_tab", "rentals");
        cookies.put("se_rs", "2754942");
        cookies.put("D_IID", "E8DB3948-F01C-37FE-BCD0-7BF86EC0E8F5");
        cookies.put("D_UID", "A001B5E7-E22B-317A-84A6-F334B28FBE1C");
        cookies.put("D_ZID", "AF013A59-178B-3515-8BEA-7CF2C66216DE");
        cookies.put("D_HID", "A57FA764-9F89-3680-815C-A616267016C2");
        cookies.put("se_lsa", "2017-07-29+16%3A49%3A49+-0400");
        cookies.put("OX_sd", "1");
        cookies.put("_dc_gtm_UA-122241-1", "1");
        cookies.put("_ga", "GA1.2.1837680605.1501348833");
        cookies.put("_gid", "GA1.2.44095435.1501348833");
        cookies.put("_gat_UA-122241-1", "1");
        cookies.put("ki_r", "");
        cookies.put(
            "se%3Asearch%3Arentals%3Astate",
            "false%7C3500%7C4000%7C%7C");
        cookies.put(
            "__gads",
            "ID=ec92b7f405e9fa75:T=1501348832:S=ALNI_MYH4" +
            "aV7aIS60Ega_i5bIGKeqSAGtQ");
        cookies.put(
            "D_SID",
            "73.35.197.188:ikKsNcL5LNRzosYi5c22rkzK1QqaELAPUesnX+W+ByQ");
        cookies.put(
            "ki_t",
            "1501348833884%3B1501348833884%3B1501358613593%3B1%3B13");
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
         * As of now, D_ZUID is the only cookie that I know needs to be updated
         * and sent.
         */
        cookies.put("D_ZUID", "A5411ED3-DD07-338D-A82F-79D0A57762FA");

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
        ArrayList<String> addresses = new ArrayList<>();
        for (int i = 1; i < split_str.length; ++i)
        {
            addresses.add(
                split_str[i].substring(0, split_str[i].indexOf("\"")));
        }

        return addresses;
    }


    public static void main(String[] args) throws IOException
    {
        final String url =
            "http://streeteasy.com/for-rent/les/price:3500-4000%7Cbeds:2";

        ArrayList<String> addresses = getAddresses(url);

        for (String address : addresses)
        {
            System.out.println(address);
        }
    }
}