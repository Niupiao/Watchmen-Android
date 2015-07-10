package niupiao.com.watchmen_android.fragments;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import niupiao.com.watchmen_android.Constants;
import niupiao.com.watchmen_android.R;
import niupiao.com.watchmen_android.activities.QrScannerActivity;
import niupiao.com.watchmen_android.utils.VolleySingleton;
import niupiao.com.watchmen_android.adapters.PropertyAdapter;
import niupiao.com.watchmen_android.models.Employee;
import niupiao.com.watchmen_android.models.Property;
import niupiao.com.watchmen_android.utils.ListingsData;

/**
 * Created by Inanity on 7/8/2015.
 */
public class ListingsFragment extends ListFragment {

    public ArrayList<Property> mProperties;
    public PropertyAdapter mAdapter;
    private Employee emp;
    private View view;
    private ImageButton mScannerButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        emp = (Employee) bundle.get("emp");
        mProperties = ListingsData.get(getActivity().getApplicationContext()).getProperties();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_listings, container, false);

        // Refresh list on swipe down
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_listings_view);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListings(true);
                Toast.makeText(getActivity(), "Refreshed Listings", Toast.LENGTH_SHORT).show();
            }
        });

        mScannerButton = (ImageButton)view.findViewById(R.id.ib_scanner);
        mScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), QrScannerActivity.class);
                intent.putExtra(Constants.IntentKeys.INTENT_KEY_FOR_EMPLOYEE, emp.getEmployee() + "");
                intent.putExtra(Constants.IntentKeys.INTENT_KEY_FOR_AUTH, emp.getAuth());
                startActivity(intent);
            }
        });

        updateView();
        return view;
    }

    public void updateView() {
        mProperties = ListingsData.get(getActivity().getApplicationContext()).getProperties();
        if(mProperties.size() > 0){
            TextView listingsExist = (TextView) view.findViewById(R.id.tv_properties_to_check);
            listingsExist.setText("");
            mAdapter = new PropertyAdapter(getActivity(), mProperties);
            ArrayList<HashMap<String, String>> propertyMap = generateHashMap(mProperties);
            SimpleAdapter adapter = new SimpleAdapter(
                    getActivity(),
                    propertyMap,
                    R.layout.list_view_property,
                    new String[]{"Property", "Task"},
                    new int[]{R.id.tv_property, R.id.tv_task_square}
            );
            ListView list = (ListView) view.findViewById(android.R.id.list);
            list.setAdapter(adapter);
        }
    }

    public ArrayList<HashMap<String, String>> generateHashMap(ArrayList<Property> props){
        ArrayList<HashMap<String, String>> propertyMaps = new ArrayList<HashMap<String, String>>();
        for (Property prop : props){
            HashMap<String, String> map = new HashMap<>();
            map.put("Property", prop.getName());
            map.put("Task", prop.getTasks().size() + "");
            propertyMaps.add(map);
        }
        return propertyMaps;
    }

    // Fetch new data from the server
    public void updateListings(boolean isSwiped) {
        String url = Constants.JsonApi.LISTINGS_URL + "&employee_id=" + emp.getEmployee() + "&auth=" + emp.getAuth();
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // Set server data as new local data
                mProperties = new ArrayList<Property>();
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Property prop = gson.fromJson(jObj.toString(), Property.class);
                        mProperties.add(prop);
                    } catch (JSONException e) {
                        Log.e("JSON Object error: ", e.toString());
                    }
                }

                updateView();
                ((SwipeRefreshLayout) getView().findViewById(R.id.refresh_listings_view)).setRefreshing(false);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        });
        // Time out
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 3000, 1, 1.0f));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
}
