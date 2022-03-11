package com.example.qrquest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback {

    TextView welcomeMessage;
    Button viewLocButton;
    Button subCodeButton;
    MapView mapView;
    GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Intent intent = getIntent();
        String username = intent.getStringExtra(CreateAccount.USER_NAME);
        //viewLocButton = findViewById(R.id.viewMapButton);
        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        mapView = findViewById(R.id.mapView);
        subCodeButton = findViewById(R.id.submitQRCodeButton);
        welcomeMessage.setText("Welcome, " + username + "!");

        //an idea
        mapView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, MainScreenActivity.class);
                startActivity(intent);
            }
        });

        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);


        // listener for the submit qr code button
        subCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanner(view);
            }
        });
    }


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

    // this is handling the qr code scanning and then saving. the actual saving is done within the QR code object. Will
    // prob need to change this when refactoring to improve cohesion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            builder.setTitle("Result");

            QRCode qrCode = new QRCode(intentResult.getContents());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // THIS NEEDS FIXING STILL
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            15f);
                }else{
                    Log.d("TAG", "onComplete: current location is null");
                    Toast.makeText(MainScreen.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d("TAG", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    // mapView life cycle
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }
}
