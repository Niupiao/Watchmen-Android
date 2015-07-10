package niupiao.com.watchmen_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zbar.Result;
import niupiao.com.watchmen_android.Constants;
import niupiao.com.watchmen_android.utils.VolleySingleton;
import niupiao.com.watchmen_android.ZBar.ZBarScannerView;


public class QrScannerActivity extends Activity implements ZBarScannerView.ResultHandler{
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("QrScanner", rawResult.getContents()); // Prints scan results
        sendDataRequest(rawResult.getContents());
    }

    private void sendDataRequest(String qr) {
        String url = Constants.JsonApi.SCANNER_URL;
        url += "&employee=" + getIntent().getStringExtra("EMPLOYEE");
        url += "&auth=" + getIntent().getStringExtra("AUTH");
        url += "&qr=" + qr;
        // Formulate the request and handle the response.
        Log.v("QrScanner", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(), QrViewerActivity.class);
                        try {
                            intent.putExtra("PROPERTY", response.getString("property"));
                            intent.putExtra("LOCATION", response.getString("location"));
                            intent.putExtra("CONTENT", response.getString("content"));
                            startActivity(intent);
                        } catch(JSONException e) {
                            Log.e("LOGIN", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
    }
}
