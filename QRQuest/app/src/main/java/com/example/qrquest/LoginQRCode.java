package com.example.qrquest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class LoginQRCode extends AppCompatActivity {
    public ImageView qrCodeImage;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_qrcode);
        //Intent intent = getIntent();
        //String emailAddress = intent.getStringExtra(ChooseQRCodeType.EMAIL_ADDRESS);
       // TextView textEmail = findViewById(R.id.textEmailAddress);
        //textEmail.setText(emailAddress);

        //Create QR Code
//        qrCodeImage = findViewById(R.id.QrcodeImage);
//        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//        Display display = manager.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        int width = point.x;
//        int height = point.y;
//        int dimen = width < height ? width : height;
//        dimen = dimen * 3 / 4;
//        String hash = computeHash(emailAddress);
//        qrgEncoder = new QRGEncoder(hash, null, QRGContents.Type.TEXT, dimen);
//        try {
//            bitmap = qrgEncoder.encodeAsBitmap();
//            qrCodeImage.setImageBitmap(bitmap);
//        } catch (WriterException e) {
//            // this method is called for
//            // exception handling.
//            Log.e("Tag", e.toString());
//        }
//        db = FirebaseFirestore.getInstance();
//        final CollectionReference collectionReference = db.collection("LoginQRCodes:");
//        HashMap<String, String> data = new HashMap<>();
//        // add tests for invalid usernames and emails later.
//        data.put("QRCode", hash);
//        collectionReference.document(emailAddress).set(data);
//    }
//    private String computeHash(String value){
//        String sha256hex = Hashing.sha256()
//                .hashString(value, StandardCharsets.UTF_8)
//                .toString();
//        return sha256hex;
  }

}