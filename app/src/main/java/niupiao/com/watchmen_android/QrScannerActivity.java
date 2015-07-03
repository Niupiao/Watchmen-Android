package niupiao.com.watchmen_android;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;


public class QrScannerActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        Toast.makeText(this.getApplicationContext(), "Scanned code " + rawResult.getText(), Toast.LENGTH_LONG).show();
    }

}
