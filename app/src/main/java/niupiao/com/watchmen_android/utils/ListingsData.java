package niupiao.com.watchmen_android.utils;

import android.content.Context;

import java.util.ArrayList;

import niupiao.com.watchmen_android.models.Property;

/**
 * A singleton class made to hold data for the lifetime of the application.
 */

public class ListingsData {
    private static ListingsData sListings;
    private Context mAppContext;
    public static String USER_KEY;
    public static String AUTH_TOKEN;

    private ArrayList<Property> mProperties; // List of properties and their relevant data.

    private ListingsData(Context appContext){
        mAppContext = appContext;
        mProperties = new ArrayList<Property>();
    }

    // Returns the ListingsData object from the Application context.
    public static ListingsData get(Context c){
        if(sListings == null) // Create a new instance if none exists.
            sListings = new ListingsData(c.getApplicationContext());
        return sListings;
    }

    public ArrayList<Property> getProperties() { return mProperties; }

    public void setProperties(ArrayList<Property> list){
        mProperties = list;
    }

    public void addProperty(Property prop) {
        mProperties.add(prop);
    }
}
