package niupiao.com.watchmen_android.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import niupiao.com.watchmen_android.VolleySingleton;
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

    public static void getListingsData(String url, final Context con){
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Gson gson = new Gson();
                ListingsData listings = ListingsData.get(con);
                for(int i = 0; i < jsonArray.length(); i++){
                    try{
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Log.d("JSON", jObj.toString());
                        Property prop = gson.fromJson(jObj.toString(), Property.class);
                        listings.addProperty(prop);
                    } catch (JSONException e) {
                        Log.e("JSON Object error: ", e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(con, volleyError.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        });

        VolleySingleton.getInstance(con).addToRequestQueue(request);
    }
}
