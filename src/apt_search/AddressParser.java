package apt_search;

import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;

public class AddressParser
{
    /**
     * The user agent for HTTP requests.
     */
    final static private String user_agent =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

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
     * Get relevant cookies.
     *
     * @return A map of cookies.
     */
    private static HashMap<String, String> getCookies() throws IOException
    {
        /*
         * Create a hashmap for cookies. We'll have to request the required
         * cookies from streeteasy.
         */
        HashMap<String, String> cookies = new HashMap<String, String>();

        /*
         * Create a POST request and add appropriate headers. This POST request
         * will get some cookies necessary for accessing the search page.
         */
        HttpPost httppost = new HttpPost("http://streeteasy.com/dstlstrt.js?PID=65B66C68-6EC9-3B18-99EC-5F2B8F8D3C20");
        httppost.addHeader(new BasicHeader("Accept", "*/*"));
        httppost.addHeader(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        httppost.addHeader(new BasicHeader("Accept-Language", "en-US,en;q=0.8"));
        httppost.addHeader(new BasicHeader("Connection", "keep-alive"));
        httppost.addHeader(new BasicHeader("Content-Type",
                                           "text/plain;charset=UTF-8"));
        httppost.addHeader(new BasicHeader("Host", "streeteasy.com"));
        httppost.addHeader(new BasicHeader("Origin", "http://streeteasy.com"));
        httppost.addHeader(new BasicHeader("Referrer",
                                           "http://streeteasy.com/for-rent/les/price:3500-4000%7Cbeds:2?page=1"));
        httppost.addHeader(new BasicHeader("User-Agent", user_agent));
        httppost.addHeader(new BasicHeader("X-Distil-Ajax", "sctbbbztucsatasbewdsrusuatrze"));
        httppost.addHeader(new BasicHeader("X-NewRelic-ID", "VQMBVl5ACQoEV1NW"));
        httppost.addHeader(new BasicHeader("Cookie", "streeteasy_site=nyc; _se_t=8cf52b3b-4823-420c-8637-9fce5170bcfa; last_search_tab=rentals; __gads=ID=8968c0d41446ffdb:T=1502287770:S=ALNI_MbESzzpsSqlK-xMSlsgwRWVTj07Vw; ki_s=152716%3A0.0.0.0.0; show_popup_count=1; OX_plg=pm; se_login_trigger=49; split=%7B%22desktop_remove_print%22%3A%22control%22%7D; anon_searcher_stage=email_captured; tracked_search=; D_IID=0049003A-7D42-3BDD-81AB-D87DA27A73BD; D_UID=0686EDED-38D6-30AD-9E5D-6A11D2EDDCFD; D_ZID=06DE0332-13FA-3849-A203-431F29B087C1; D_ZUID=A2E77FF6-C5CA-34F7-8C25-08A1964DD8F6; D_HID=0A76028F-7AA5-3134-9AAD-480DBD89C536; se_lsa=2017-08-14+13%3A03%3A14+-0400; se_rs=2754942%2C2852797%2C2590737%2C2609760%2C2754942; _ses=BAh7DUkiD3Nlc3Npb25faWQGOgZFVEkiJWYwNjZlMzY5NzUzMDllMWNmMzg0YjVjOGIzMmIzYzdmBjsAVEkiEG5ld192aXNpdG9yBjsARlRJIg51c2VyX2RhdGEGOwBGexA6EHNhbGVzX29yZGVySSIPcHJpY2VfZGVzYwY7AFQ6EnJlbnRhbHNfb3JkZXJJIg9wcmljZV9kZXNjBjsAVDoQaW5fY29udHJhY3RGOg1oaWRlX21hcEY6EnNob3dfbGlzdGluZ3NGOhJtb3J0Z2FnZV90ZXJtaSM6GW1vcnRnYWdlX2Rvd25wYXltZW50aRk6IW1vcnRnYWdlX2Rvd25wYXltZW50X2RvbGxhcnNpAlDDOhJtb3J0Z2FnZV9yYXRlZgkzLjkxOhNsaXN0aW5nc19vcmRlckkiEGxpc3RlZF9kZXNjBjsAVDoQc2VhcmNoX3ZpZXdJIgxkZXRhaWxzBjsAVEkiEmxvb2tfYW5kX2ZlZWwGOwBGSSIJMjAxNAY7AFRJIhFsYXN0X3NlY3Rpb24GOwBGSSIMcmVudGFscwY7AFRJIhBsYXN0X3NlYXJjaAY7AEZpA34JKkkiEF9jc3JmX3Rva2VuBjsARkkiMWJrQ1cvY0YrU3ZSYjloRnJpS0hIRHpNL1NOclFuZlhPYktnMDMybm1mQjg9BjsARkkiCHBpcwY7AEZpRg%3D%3D--109d201991d8a5f4167f7d27ce6552f4c4f2d3de; OX_sd=1; se%3Asearch%3Ashared%3Astate=109%2C300%7C2%7C%7Cfalse; se%3Asearch%3Arentals%3Astate=false%7C3500%7C4000%7C%7C; _mibhv=anon-1502287771887-3362718206_6815; _dc_gtm_UA-122241-1=1; _gat_UA-122241-1=1; _ga=GA1.2.2125464202.1502287772; _gid=GA1.2.1916942005.1502660496; ki_t=1502287772620%3B1502726208062%3B1502730197065%3B3%3B36; ki_r="));
        final StringEntity se = new StringEntity("p=%7B%22proof%22%3A%22f4%3A1502771432253%3A7zP35LHn4ZTtmg2tKKE6%22%2C%22fp2%22%3A%7B%22userAgent%22%3A%22Mozilla%2F5.0(X11%3BLinuxx86_64)AppleWebKit%2F537.36(KHTML%2ClikeGecko)Chrome%2F58.0.3029.110Safari%2F537.36%22%2C%22language%22%3A%22en-US%22%2C%22screen%22%3A%7B%22width%22%3A1920%2C%22height%22%3A1080%2C%22availHeight%22%3A1056%2C%22availWidth%22%3A1920%7D%2C%22timezone%22%3A-7%2C%22indexedDb%22%3Atrue%2C%22addBehavior%22%3Afalse%2C%22openDatabase%22%3Atrue%2C%22cpuClass%22%3A%22unknown%22%2C%22platform%22%3A%22Linuxx86_64%22%2C%22doNotTrack%22%3A%22unknown%22%2C%22plugins%22%3A%22ChromePDFViewer%3A%3A%3A%3Aapplication%2Fpdf~pdf%3BChromePDFViewer%3A%3APortableDocumentFormat%3A%3Aapplication%2Fx-google-chrome-pdf~pdf%3BNativeClient%3A%3A%3A%3Aapplication%2Fx-nacl~%2Capplication%2Fx-pnacl~%3BWidevineContentDecryptionModule%3A%3AEnablesWidevinelicensesforplaybackofHTMLaudio%2Fvideocontent.(version%3A1.4.8.977)%3A%3Aapplication%2Fx-ppapi-widevine-cdm~%22%2C%22canvas%22%3A%7B%22winding%22%3A%22yes%22%2C%22towebp%22%3Atrue%2C%22blending%22%3Atrue%2C%22img%22%3A%22e567ca20fc7c330335ce3e59af0ad671f45eafcb%22%7D%2C%22webGL%22%3A%7B%22img%22%3A%22ec1ac927598cc32e395530f37437b13e3d7a4bdc%22%2C%22extensions%22%3A%22ANGLE_instanced_arrays%3BEXT_blend_minmax%3BEXT_frag_depth%3BEXT_shader_texture_lod%3BEXT_sRGB%3BEXT_texture_filter_anisotropic%3BWEBKIT_EXT_texture_filter_anisotropic%3BOES_element_index_uint%3BOES_standard_derivatives%3BOES_texture_float%3BOES_texture_float_linear%3BOES_texture_half_float%3BOES_texture_half_float_linear%3BOES_vertex_array_object%3BWEBGL_compressed_texture_astc%3BWEBGL_compressed_texture_s3tc%3BWEBKIT_WEBGL_compressed_texture_s3tc%3BWEBGL_debug_renderer_info%3BWEBGL_debug_shaders%3BWEBGL_depth_texture%3BWEBKIT_WEBGL_depth_texture%3BWEBGL_draw_buffers%3BWEBGL_lose_context%3BWEBKIT_WEBGL_lose_context%22%2C%22aliasedlinewidthrange%22%3A%22%5B1%2C7.375%5D%22%2C%22aliasedpointsizerange%22%3A%22%5B1%2C255%5D%22%2C%22alphabits%22%3A8%2C%22antialiasing%22%3A%22yes%22%2C%22bluebits%22%3A8%2C%22depthbits%22%3A24%2C%22greenbits%22%3A8%2C%22maxanisotropy%22%3A16%2C%22maxcombinedtextureimageunits%22%3A192%2C%22maxcubemaptexturesize%22%3A8192%2C%22maxfragmentuniformvectors%22%3A4096%2C%22maxrenderbuffersize%22%3A8192%2C%22maxtextureimageunits%22%3A32%2C%22maxtexturesize%22%3A8192%2C%22maxvaryingvectors%22%3A32%2C%22maxvertexattribs%22%3A16%2C%22maxvertextextureimageunits%22%3A32%2C%22maxvertexuniformvectors%22%3A4096%2C%22maxviewportdims%22%3A%22%5B8192%2C8192%5D%22%2C%22redbits%22%3A8%2C%22renderer%22%3A%22WebKitWebGL%22%2C%22shadinglanguageversion%22%3A%22WebGLGLSLES1.0(OpenGLESGLSLES1.0Chromium)%22%2C%22stencilbits%22%3A0%2C%22vendor%22%3A%22WebKit%22%2C%22version%22%3A%22WebGL1.0(OpenGLES2.0Chromium)%22%2C%22vertexshaderhighfloatprecision%22%3A23%2C%22vertexshaderhighfloatprecisionrangeMin%22%3A127%2C%22vertexshaderhighfloatprecisionrangeMax%22%3A127%2C%22vertexshadermediumfloatprecision%22%3A23%2C%22vertexshadermediumfloatprecisionrangeMin%22%3A127%2C%22vertexshadermediumfloatprecisionrangeMax%22%3A127%2C%22vertexshaderlowfloatprecision%22%3A23%2C%22vertexshaderlowfloatprecisionrangeMin%22%3A127%2C%22vertexshaderlowfloatprecisionrangeMax%22%3A127%2C%22fragmentshaderhighfloatprecision%22%3A23%2C%22fragmentshaderhighfloatprecisionrangeMin%22%3A127%2C%22fragmentshaderhighfloatprecisionrangeMax%22%3A127%2C%22fragmentshadermediumfloatprecision%22%3A23%2C%22fragmentshadermediumfloatprecisionrangeMin%22%3A127%2C%22fragmentshadermediumfloatprecisionrangeMax%22%3A127%2C%22fragmentshaderlowfloatprecision%22%3A23%2C%22fragmentshaderlowfloatprecisionrangeMin%22%3A127%2C%22fragmentshaderlowfloatprecisionrangeMax%22%3A127%2C%22vertexshaderhighintprecision%22%3A0%2C%22vertexshaderhighintprecisionrangeMin%22%3A31%2C%22vertexshaderhighintprecisionrangeMax%22%3A30%2C%22vertexshadermediumintprecision%22%3A0%2C%22vertexshadermediumintprecisionrangeMin%22%3A31%2C%22vertexshadermediumintprecisionrangeMax%22%3A30%2C%22vertexshaderlowintprecision%22%3A0%2C%22vertexshaderlowintprecisionrangeMin%22%3A31%2C%22vertexshaderlowintprecisionrangeMax%22%3A30%2C%22fragmentshaderhighintprecision%22%3A0%2C%22fragmentshaderhighintprecisionrangeMin%22%3A31%2C%22fragmentshaderhighintprecisionrangeMax%22%3A30%2C%22fragmentshadermediumintprecision%22%3A0%2C%22fragmentshadermediumintprecisionrangeMin%22%3A31%2C%22fragmentshadermediumintprecisionrangeMax%22%3A30%2C%22fragmentshaderlowintprecision%22%3A0%2C%22fragmentshaderlowintprecisionrangeMin%22%3A31%2C%22fragmentshaderlowintprecisionrangeMax%22%3A30%7D%2C%22touch%22%3A%7B%22maxTouchPoints%22%3A0%2C%22touchEvent%22%3Afalse%2C%22touchStart%22%3Afalse%7D%2C%22video%22%3A%7B%22ogg%22%3A%22probably%22%2C%22h264%22%3A%22probably%22%2C%22webm%22%3A%22probably%22%7D%2C%22audio%22%3A%7B%22ogg%22%3A%22probably%22%2C%22mp3%22%3A%22probably%22%2C%22wav%22%3A%22probably%22%2C%22m4a%22%3A%22maybe%22%7D%2C%22fonts%22%3A%22%22%7D%2C%22cookies%22%3A1%2C%22setTimeout%22%3A1%2C%22setInterval%22%3A1%2C%22appName%22%3A%22Netscape%22%2C%22platform%22%3A%22Linuxx86_64%22%2C%22syslang%22%3A%22en-US%22%2C%22userlang%22%3A%22en-US%22%2C%22cpu%22%3A%22%22%2C%22productSub%22%3A%2220030107%22%2C%22plugins%22%3A%7B%220%22%3A%22ChromePDFViewer%22%2C%221%22%3A%22WidevineContentDecryptionModule%22%2C%222%22%3A%22NativeClient%22%2C%223%22%3A%22ChromePDFViewer%22%7D%2C%22mimeTypes%22%3A%7B%220%22%3A%22application%2Fpdf%22%2C%221%22%3A%22WidevineContentDecryptionModuleapplication%2Fx-ppapi-widevine-cdm%22%2C%222%22%3A%22NativeClientExecutableapplication%2Fx-nacl%22%2C%223%22%3A%22PortableNativeClientExecutableapplication%2Fx-pnacl%22%2C%224%22%3A%22PortableDocumentFormatapplication%2Fx-google-chrome-pdf%22%7D%2C%22screen%22%3A%7B%22width%22%3A1920%2C%22height%22%3A1080%2C%22colorDepth%22%3A24%7D%2C%22fonts%22%3A%7B%220%22%3A%22monospace%22%2C%221%22%3A%22DejaVuSerif%22%2C%222%22%3A%22DejaVuSans%22%2C%223%22%3A%22DejaVuSansMono%22%2C%224%22%3A%22LiberationMono%22%2C%225%22%3A%22CourierNew%22%2C%226%22%3A%22Courier%22%7D%7D");
        httppost.setEntity(se);

        /*
         * Get the response and set the other cookies in the hash map.
         */
        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response = httpclient.execute(httppost);
        Header[] headers = response.getHeaders("Set-Cookie");
        for (Header header : headers)
        {
            /*
             * Split the cookie name and content.
             */
            final String value = header.getValue();
            final String[] split_equals = value.split("=");
            final String cookie_name = split_equals[0];
            final String[] split_semi = split_equals[1].split(";");
            final String cookie_value = split_semi[0];
            cookies.put(cookie_name, cookie_value);
            System.out.println(cookie_name + ": " + cookie_value);
        }

        return cookies;
    }

    /**
     * Scrape a given URL for addresses.
     *
     * @param url The URL to scrape.
     * @param neighborhood The neighborhood to search.
     *
     * @return A HashSet of UrlAddress objects.
     */
    private static HashSet<UrlAddress> getAddresses(final String url,
                                                    final String neighborhood)
        throws IOException
    {
        /*
         * Connect to the URL.
         */
        HashMap<String, String> cookies = getCookies();
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
         * Throw an IOException if we have an invalid page.
         */
        if (split_str.length <= 1)
            throw new IOException("Invalid streeteasy page.");

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

            if (neighborhood.toLowerCase() == "brooklyn")
                addr.address += " Brooklyn, NY";
            else
                addr.address += " New York, NY";
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
        int index = 1;
        while (true)
        {
            try
            {
                addresses.addAll(
                    getAddresses(generateUrl(neighborhood,
                                             min_price,
                                             max_price,
                                             num_beds,
                                             index),
                                 neighborhood));
            }
            catch (org.jsoup.HttpStatusException e)
            {
                break;
            }
            catch (IOException e)
            {
                break;
            }

            ++index;
        }

        return addresses;
    }
}