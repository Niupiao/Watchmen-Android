package niupiao.com.watchmen_android;

/**
 * Created by Inanity on 7/6/2015.
 */
public class Constants {

    /**
     * Contains parameter keys used for POST requests.
     * The reason we have these all together in one file is to easily ensure they
     * match the parameter keys that the JSON API expects.
     */
    public final static class JsonApi {
        public static final String BASE_URL = "https://watchmen.herokuapp.com";
        public static final String LOGIN_URL = BASE_URL + "/auth?format=json";
        public static final String LISTINGS_URL = BASE_URL + "/listings?format=json";
        public static final String SCANNER_URL = BASE_URL + "/logs/new?format=json";
    }
}
