package com.example.qrquest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback{
    public static final String USER_ID = "com.example.qrquest.USERID";
    public static final String USER_DISPLAY = "com.example.qrquest.USERDISPLAY";
    private static final String TAG = "MainScreen";
    TextView welcomeMessage;
    boolean isPermissionGranted;
    MapView mapView;
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    Button generateQRCode;
    String userID;
    String userDisplay;
    Button subCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_main_screen);
        Bundle intent = getIntent().getExtras();
        if (intent == null) {
            loginWithFile();
        }
        else{
            if (intent.containsKey("USER_NAME_MainActivity")){
                userID = intent.getString("USER_ID_MainActivity");
                userDisplay = intent.getString("USER_DISPLAY_MainActivity");
            }
            else{
                userID = intent.getString("USER_ID_CreateAccount");
                userDisplay = intent.getString("USER_DISPLAY_CreateAccount");
            }
        }
        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        welcomeMessage.setText("Welcome, " + userDisplay.substring(0, 8) + "!"); // qol change
        subCodeButton = findViewById(R.id.submitQRCodeButton);
        generateQRCode = findViewById(R.id.generateQRCodeButton);
        // qrcode logic
        generateQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseQRCodeType = new Intent(MainScreen.this, ChooseQRCodeType.class);
                chooseQRCodeType.putExtra(USER_ID, userID);
                chooseQRCodeType.putExtra(USER_DISPLAY, userDisplay);
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

        //map logic
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    /**
     * If login file exists, read the username and email from it, else generate username and email and create file.
     * Reference: शु-Bham at https://stackoverflow.com/questions/12116092/android-random-string-generator
     */
    private void loginWithFile(){
        userID = null;
        userDisplay = null;

        File file = new File(this.getFilesDir(), "login.txt");
        try {

            if (file.exists()){
                FileReader fileReader = new FileReader(file);
                char[] buffer = new char[100];

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                userDisplay = bufferedReader.readLine();
                Log.i("em", userDisplay);
                userID = bufferedReader.readLine();
                Log.i("un", userID);
            }
            else{

                FileWriter fileWriter = new FileWriter(file.getPath());

                userID = UUID.randomUUID().toString();
                userDisplay = UUID.randomUUID().toString(); // TODO: check if this username or email already exists (unlikely)
                Log.i("userID", userID);
                Log.i("email", userDisplay);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference collectionReference = db.collection("Users");
                HashMap<String, String> data = new HashMap<>();
                // add tests for invalid usernames and emails later.
                data.put("Username", userID);
                collectionReference.document(userDisplay).set(data);

                file.createNewFile(); // Create the login file
                fileWriter.write(userID);
                fileWriter.append("\n"+userDisplay);
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
}