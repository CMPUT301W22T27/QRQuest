package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
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
    Button privateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_status_qrcode);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChooseQRCodeType.USER_NAME);
        String emailAddress = intent.getStringExtra(ChooseQRCodeType.EMAIL_ADDRESS);
        TextView textUsername = findViewById(R.id.gameStatusUsername);
        textUsername.setText("Username: "+ username);

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
        String hash = computeHash(username);
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
        collectionReference.document(username).set(data);



    }
    private String computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileMenuItem:
                startActivity(new Intent(this, GameStatusQRCode.class));
                return true;

        }
        switch (item.getItemId()) {
            case R.id.homeMenuItem:
                startActivity(new Intent(this, MainScreen.class));
                return true;
        }



        return(super.onOptionsItemSelected(item));
    }


    public void next_page(View view) {
        Intent intent = new Intent(view.getContext(), LoginQRCode.class);
        startActivity(intent);
    }

}