package com.example.qrquest;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

public class Scanner {
    View view;
    Activity activity;
    public Scanner(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    /**
     * Initiates the Scanner
     * Reference: https://www.youtube.com/watch?v=u2pgSu9RhYo
     */
    public void startScan(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                activity
        );
        intentIntegrator.setPrompt("Uses the volume up/down to turn flash on/off");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();

        Toast.makeText(activity.getApplicationContext(), "this is shown when the scan is done", Toast.LENGTH_SHORT).show();

    }
}
