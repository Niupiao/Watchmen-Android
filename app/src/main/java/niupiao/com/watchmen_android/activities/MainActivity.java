package niupiao.com.watchmen_android.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import niupiao.com.watchmen_android.R;
import niupiao.com.watchmen_android.fragments.ListingsFragment;
import niupiao.com.watchmen_android.models.Employee;


public class MainActivity extends ActionBarActivity {

    private final String INTENT_KEY_FOR_EMPLOYEE = "employee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Watchmen");

        Intent intent = getIntent();
        Employee emp = intent.getParcelableExtra(INTENT_KEY_FOR_EMPLOYEE);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ListingsFragment frag = new ListingsFragment();
        Bundle listingBundle = new Bundle();
        listingBundle.putParcelable("emp", emp);
        frag.setArguments(listingBundle);
        ft.add(R.id.fl_listings_container, frag);
        ft.commit();
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


    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
