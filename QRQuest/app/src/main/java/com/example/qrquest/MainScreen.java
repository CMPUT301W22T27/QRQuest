package com.example.qrquest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

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
        if (intent == null) {
            loginWithFile();
        }
        else{
            if (intent.containsKey("USER_NAME_MainActivity")){
                username = intent.getString("USER_NAME_MainActivity");
                email = intent.getString("EMAIL_ADDRESS_MainActivity");
            }
            else{
                username = intent.getString("USER_NAME_CreateAccount");
                email = intent.getString("EMAIL_ADDRESS_CreateAccount");
            }
        }

        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        welcomeMessage.setText("Welcome, " + username + "!");

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
                scanner(view);
            }
        });

        // sidebar logic
    }



    /**
     * If login file exists, read the username and email from it, else generate username and email and create file.
     * Reference: शु-Bham at https://stackoverflow.com/questions/12116092/android-random-string-generator
     */
    private void loginWithFile(){
        username = null;
        email = null;

        File file = new File(this.getFilesDir(), "login.txt");
        try {

            if (file.exists()){
                FileReader fileReader = new FileReader(file);
                char[] buffer = new char[100];

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                email = bufferedReader.readLine();
                Log.i("em", email);
                username = bufferedReader.readLine();
                Log.i("un", username);
            }
            else{

                FileWriter fileWriter = new FileWriter(file.getPath());

                username = UUID.randomUUID().toString();
                email = UUID.randomUUID().toString(); // TODO: check if this username or email already exists (unlikely)
                Log.i("username", username);
                Log.i("email", email);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference collectionReference = db.collection("Users");
                HashMap<String, String> data = new HashMap<>();
                // add tests for invalid usernames and emails later.
                data.put("Username", username);
                collectionReference.document(email).set(data);

                file.createNewFile(); // Create the login file
                fileWriter.write(email);
                fileWriter.append("\n"+username);
                fileWriter.close();
            }

        } catch (IOException e) {
            Log.e("Error:", "File error");
            // TODO: Error occurred when opening raw file for reading.
        }
    }

    /**
     * Accesses camera and scans qrcode
     * Reference: https://www.youtube.com/watch?v=u2pgSu9RhYo
     * @param view view
     */
    public void scanner(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                MainScreen.this
        );
        intentIntegrator.setPrompt("Uses the volume up/down to turn flash on/off");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();


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