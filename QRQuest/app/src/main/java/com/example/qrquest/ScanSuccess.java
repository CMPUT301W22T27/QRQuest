package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanSuccess extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;
    Button takePictureButton;
    TextView scoreDisplay;
    Button submitButton;
    QRCode qrCode;
    EditText qrNameBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
        imageView = findViewById(R.id.pictureView);
        takePictureButton = findViewById(R.id.photoButton);
        submitButton = findViewById(R.id.submitButton);
        qrNameBox = findViewById(R.id.qrName);


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