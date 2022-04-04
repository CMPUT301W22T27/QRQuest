package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;

import java.util.HashMap;

public class ScanSuccess extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;
    Button takePictureButton;
    TextView scoreDisplay;
    Button submitButton;
    QRCode qrCode;
    EditText qrNameBox;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
        imageView = findViewById(R.id.pictureView);
        takePictureButton = findViewById(R.id.photoButton);
        submitButton = findViewById(R.id.submitButton);
        qrNameBox = findViewById(R.id.qrName);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final CollectionReference collection = db.collection("QRCodeLocs");
                if (location != null) {
                    String hash = qrCode.getHash();
                    HashMap<String, String> data = new HashMap<>();
                    // add tests for invalid usernames and emails later.
                    data.put("Latitude", Double.toString(location.getLatitude()));
                    data.put("Longitude", Double.toString(location.getLongitude()));
                    data.put("Score", Integer.toString(qrCode.getScore()));
                    collection.document(hash).set(data);
                }
            }
        });

//        Bundle extras = getIntent().getExtras();
//        String score = extras.getString("score");

        qrCode = (QRCode) getIntent().getSerializableExtra("QRCODE");

        scoreDisplay = findViewById(R.id.scoreView);
        scoreDisplay.setText("Score: " + qrCode.getScore());

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qrName = qrNameBox.getText().toString();
                if(qrName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "QR Code needs to be named.", Toast.LENGTH_SHORT).show();
                } else {
                    qrCode.setName(qrName);
                    qrCode.save();
                    Toast.makeText(getApplicationContext(), "Submission Successful", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            qrCode.setImage(imageBitmap);

            //this is the code to decode the text that is stored in the database. just retrieve whats in the image field"
//            String encodedImage = qrCode.getImage();
//            byte[] decodedArray = Base64.decode(encodedImage, Base64.DEFAULT);
//            Bitmap image = BitmapFactory.decodeByteArray(decodedArray, 0, decodedArray.length);
//            imageView.setImageBitmap(image);


        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
}