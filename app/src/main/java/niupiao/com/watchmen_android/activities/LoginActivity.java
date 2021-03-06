package niupiao.com.watchmen_android.activities;

import android.content.Intent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import niupiao.com.watchmen_android.Constants;
import niupiao.com.watchmen_android.R;
import niupiao.com.watchmen_android.utils.VolleySingleton;
import niupiao.com.watchmen_android.models.Employee;
import niupiao.com.watchmen_android.utils.ListingsData;


public class LoginActivity extends ActionBarActivity {
    private Button mLoginButton;
    private CheckBox mRememberCheckBox;
    private EditText mIdField;
    private EditText mPasswordField;
    private LinearLayout ll;
    private View logo;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ll = (LinearLayout) findViewById(R.id.sliding_ll);
        logo = findViewById(R.id.image);
        loader = (LinearLayout) findViewById(R.id.loading_circle);
        mIdField = (EditText) findViewById(R.id.username_et);
        mPasswordField = (EditText) findViewById(R.id.password_et);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                startLoginAnimation();

                // Delayed start
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendLoginRequest();
                    }
                }, 1900);
            }
        });

        mRememberCheckBox = (CheckBox) findViewById(R.id.remember_checkbox);

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        if (settings.getBoolean("rememberLogin", false)) {
            mIdField.setText(settings.getString("login", ""));
            mPasswordField.requestFocus();
            mRememberCheckBox.setChecked(settings.getBoolean("rememberLogin", false));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("rememberLogin", mRememberCheckBox.isChecked());
        if (mRememberCheckBox.isChecked()) {
            editor.putString("login", mIdField.getText().toString());
        }
        // Commit the edits!
        editor.commit();
    }

    private void startLoginAnimation() {
        // Transition to loading animation
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(400);
        fadeOut.setFillAfter(true);
        ll.startAnimation(fadeOut);
        ll.setVisibility(View.GONE);

        // Fade in loader
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(300);
        fadeIn.setStartOffset(300);
        fadeIn.setFillAfter(true);
        loader.startAnimation(fadeIn);
        loader.setVisibility(View.VISIBLE);

        // Move logo down
        Animation moveDown = new TranslateAnimation(0, 0, 0, 200);
        moveDown.setDuration(600);
        moveDown.setStartOffset(200);
        moveDown.setInterpolator(new DecelerateInterpolator());
        moveDown.setFillAfter(true);
        logo.startAnimation(moveDown);
    }

    // Reverse prior animation
    private void returnToLogin() {

        // Fade out loader
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(150);
        fadeOut.setFillAfter(true);
        loader.clearAnimation();
        loader.startAnimation(fadeOut);
        loader.setVisibility(View.GONE);

        // Move logo up
        Animation moveUp = new TranslateAnimation(0, 0, 200, 0);
        moveUp.setDuration(600);
        moveUp.setStartOffset(200);
        moveUp.setInterpolator(new DecelerateInterpolator());
        moveUp.setFillAfter(true);
        logo.startAnimation(moveUp);

        // Fade textviews in
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(300);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        ll.startAnimation(fadeIn);
        ll.setVisibility(View.VISIBLE);
    }

    // Create and send login request to server
    private void sendLoginRequest() {

        String url = Constants.JsonApi.LOGIN_URL;
        url += "&username=" + mIdField.getText();
        url += "&password=" + mPasswordField.getText();
        // Formulate the request and handle the response.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(Constants.IntentKeys.INTENT_KEY_FOR_AUTH, response.toString());
                        try {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Employee employee = login(response);
                            intent.putExtra(Constants.IntentKeys.INTENT_KEY_FOR_EMPLOYEE, employee);
                            intent.putExtra(Constants.IntentKeys.INTENT_KEY_FOR_AUTH, response.getString("auth"));
                            updateListings(employee, intent);

                            finish();
                        } catch(JSONException e) {
                            try {
                                Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                                returnToLogin();
                            }
                            catch (JSONException f) {
                                Toast.makeText(getApplicationContext(), f.toString(), Toast.LENGTH_LONG).show();
                                returnToLogin();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        returnToLogin();
                    }
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
    }

    private Employee login(JSONObject serverResponse){
        Gson gson = new Gson();
        Employee emp = gson.fromJson(serverResponse.toString(), Employee.class);
        return emp;
    }

    private void updateListings(Employee emp, Intent intent){
        // Update Listings with Employee Data
        String listingsURL = Constants.JsonApi.LISTINGS_URL + "&employee_id=" + emp.getEmployee() + "&auth=" + emp.getAuth();
        ListingsData listings = ListingsData.get(getApplicationContext());
        listings.clear();
        listings.loginAndGetListings(listingsURL, intent, this);
    }
}
