package com.example.qrquest;

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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * GameStatusQRCode
 * Extends AppCompatActivity
 */
public class GameStatusQRCode extends AppCompatActivity {
    public ImageView GameStatusQRCodeImage;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_status_qrcode);
        Intent intent = getIntent();
        String userID = intent.getStringExtra(ChooseQRCodeType.USER_ID);
        String userDisplay = intent.getStringExtra(ChooseQRCodeType.USER_DISPLAY);
        TextView textUsername = findViewById(R.id.gameStatusUsername);
        textUsername.setText("Username: "+ userDisplay.substring(0, 8));

        //Create QR Code
        GameStatusQRCodeImage = findViewById(R.id.GameStatusQRCode);
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        String hash = computeHash(userID);
        qrgEncoder = new QRGEncoder(hash, null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            GameStatusQRCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("GameStatusQRCodes:");
        HashMap<String, String> data = new HashMap<>();
        // add tests for invalid usernames and emails later.
        data.put("QRCode", hash);
        collectionReference.document(userID).set(data);
    }
    private String computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
    }
}