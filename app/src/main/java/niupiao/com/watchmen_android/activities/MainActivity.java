package niupiao.com.watchmen_android.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import niupiao.com.watchmen_android.Constants;
import niupiao.com.watchmen_android.R;
import niupiao.com.watchmen_android.VolleySingleton;
import niupiao.com.watchmen_android.adapters.PropertyAdapter;
import niupiao.com.watchmen_android.models.Employee;
import niupiao.com.watchmen_android.models.Property;
import niupiao.com.watchmen_android.utils.ListingsData;


public class MainActivity extends ActionBarActivity {

    public PropertyAdapter mAdapter;
    public ArrayList<Property> mProperties;

    private final String INTENT_KEY_FOR_EMPLOYEE = "employee";
    private final String INTENT_KEY_FOR_AUTH = "auth";
    private final Context context = this;

    //private ListingsData listings = ListingsData.get(getApplicationContext()); // Does this make sense?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Employee emp = intent.getParcelableExtra(INTENT_KEY_FOR_EMPLOYEE);

        String url = Constants.JsonApi.LISTINGS_URL + "employee_id=" + emp.getEmployee() + "&auth=" + emp.getAuth();
        Log.d("LIST", url);
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Gson gson = new Gson();
                ListingsData listings = ListingsData.get(getApplicationContext());
                for(int i = 0; i < jsonArray.length(); i++){
                    try{
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Log.d("JSON", jObj.toString());
                        Property prop = gson.fromJson(jObj.toString(), Property.class);
                        listings.addProperty(prop);
                    } catch (JSONException e) {
                        Log.e("JSON Object error: ", e.toString());
                    }
                    updateList();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void updateList() {
        mProperties = ListingsData.get(this).getProperties();
        if(mProperties.size() > 0){
            TextView listingsExist = (TextView) findViewById(R.id.tv_properties_to_check);
            listingsExist.setText("");
            mAdapter = new PropertyAdapter(this, mProperties);
            ArrayList<HashMap<String, String>> propertyMap = generateHashMap(mProperties);
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    propertyMap,
                    R.layout.list_view_property,
                    new String[]{"Normal"},
                    new int[]{R.id.tv_property}
            );
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
        }
    }

    public ArrayList<HashMap<String, String>> generateHashMap(ArrayList<Property> props){
        ArrayList<HashMap<String, String>> propertyMaps = new ArrayList<HashMap<String, String>>();
        for (Property prop : props){
            HashMap<String, String> map = new HashMap<>();
            map.put("Normal", prop.getLocation());
            propertyMaps.add(map);
        }
        return propertyMaps;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.i("Barcode Result", contents);
                Intent i1 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i1);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("Barcode Result", "Result canceled");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
