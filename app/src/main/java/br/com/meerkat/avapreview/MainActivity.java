package br.com.meerkat.avapreview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.com.meerkat.ava.Ava;


public class MainActivity extends Activity {
    private CameraPreviewSurface preview = null;
    private SurfaceOverlay overlay;

    private static final int REQUEST_CAMERA_RESULT = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 112;
    private static final String TAG = "MainActivity";
    private Tracker mTracker;
    private static long mUptime;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        boolean hasWritePermission = false;
        boolean hasCameraPermission = false;

        // should request permission if android api > 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWritePermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            if(hasWritePermission == false) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);

                while(hasWritePermission == false) {
                    try {
                        Thread.sleep(50);                 //1000 milliseconds is one second.
                        hasWritePermission = (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            hasCameraPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED);
            if (hasCameraPermission == false) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);

                while(hasCameraPermission == false) {
                    try {
                        Thread.sleep(50);                 //1000 milliseconds is one second.
                        hasCameraPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            Ava.copyLandmarkModel(this);
            setContentView(R.layout.activity_main);

            overlay = (SurfaceOverlay) findViewById(R.id.surfaceOverlayView);
            preview = (CameraPreviewSurface) findViewById(R.id.surfaceView);
            preview.linkOverlay(overlay);
            preview.setTextView((TextView)findViewById(R.id.statusText));

        } else {
            Ava.copyLandmarkModel(this);

            setContentView(R.layout.activity_main);

            overlay = (SurfaceOverlay) findViewById(R.id.surfaceOverlayView);
            preview = (CameraPreviewSurface) findViewById(R.id.surfaceView);
            preview.linkOverlay(overlay);
            preview.setTextView((TextView) findViewById(R.id.statusText));
        }

        final ImageButton button = (ImageButton) findViewById(R.id.changeCamButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "Change camera Button clicked!");
                preview.changeCamera();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot run application because camera service permission have not been granted", Toast.LENGTH_SHORT).show();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot run application because write permission have not been granted", Toast.LENGTH_SHORT).show();
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(preview != null) {
            preview.closeCamera();
            preview = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://br.com.meerkat.avapreview/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        mUptime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUptime = System.currentTimeMillis();
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "Pausing");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Pausing")
                .setValue(System.currentTimeMillis()-mUptime)
                .build());
    }

    @Override
    public void onStop() {

        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://br.com.meerkat.avapreview/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();

        Log.i(TAG, "Stopping");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Stopping")
                .setValue(System.currentTimeMillis()-mUptime)
                .build());
    }
}
