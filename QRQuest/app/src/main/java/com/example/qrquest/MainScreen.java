package com.example.qrquest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback{
    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    TextView welcomeMessage;
    boolean isPermissionGranted;
    MapView mapView;
    Button generateQRCode;
    String username;
    String email;
    Button subCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (intent.containsKey("USER_NAME_MainActivity")){
                username = intent.getString("USER_NAME_MainActivity");
                email = intent.getString("EMAIL_ADDRESS_MainActivity");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username + "!");
            }
            else{
                username = intent.getString("USER_NAME_CreateAccount");
                email = intent.getString("EMAIL_ADDRESS_CreateAccount");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username + "!!!!");
            }
        }

        subCodeButton = findViewById(R.id.submitQRCodeButton);
        generateQRCode = findViewById(R.id.generateQRCodeButton);
        mapView = (MapView) findViewById(R.id.appMapView);

        // map logic
        checkPermission();
        // dummy check for permission; need to add more details here
        if (isPermissionGranted){
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);
        }

        // qrcode logic
        generateQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseQRCodeType = new Intent(MainScreen.this, ChooseQRCodeType.class);
                chooseQRCodeType.putExtra(USER_NAME, username);
                chooseQRCodeType.putExtra(EMAIL_ADDRESS,email);
                startActivity(chooseQRCodeType);
            }
        });

        // listener for the submit qr code button
        subCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scanner scanner = new Scanner(view, MainScreen.this);
                scanner.startScan();
            }
        });

        // sidebar logic
    }



    //need to change this when refactoring to improve cohesion

    /**
     * this is handling the qr code scanning and then saving. the actual saving is done within the QR code object.
     * @param requestCode request Code
     * @param resultCode result code
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            builder.setTitle("Result");

            QRCode qrCode = new QRCode(intentResult.getContents(), false);
            String score = Integer.toString(qrCode.getScore());
            builder.setMessage(score);
            qrCode.saveScore(); // this should really be a user.saveCode(qrCode)


//            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            builder.show();

        }else {
            Toast.makeText(getApplicationContext(), "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * makes the menu (the 3 dots in the upper right hand side) appear on the main screen page
     * @param menu menu
     * @return true
     */


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    /**method for dummy check
     *
     */


    private void checkPermission(){
        isPermissionGranted = true;
        return;
    }

    // MAP LIFE CYCLE METHODS
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // logic for how the map pans to device location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}