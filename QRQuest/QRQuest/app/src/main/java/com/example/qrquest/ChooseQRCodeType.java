package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * QR code functionality for the log in screen
 */

public class ChooseQRCodeType extends AppCompatActivity {
    public static final String USER_ID = "com.example.qrquest.USERID";
    public static final String USER_DISPLAY = "com.example.qrquest.USERDISPLAY";
    Button generateLoginCode;
    Button generateGameStatusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_qrcode_type);
        Intent intent = getIntent();
        generateLoginCode = findViewById(R.id.loginCodeButton);
        generateGameStatusCode = findViewById(R.id.gameStatusCodeButton);
        String userID = intent.getStringExtra(MainScreen.USER_ID);
        String userDisplay = intent.getStringExtra(MainScreen.USER_DISPLAY);

        generateLoginCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent loginQrCode = new Intent(ChooseQRCodeType.this, LoginQRCode.class);
                loginQrCode.putExtra(USER_ID, userID);
                loginQrCode.putExtra(USER_DISPLAY, userDisplay);
                startActivity(loginQrCode);
            }
        });

        generateGameStatusCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent gameStatusQrCode = new Intent(ChooseQRCodeType.this, GameStatusQRCode.class);
                gameStatusQrCode.putExtra(USER_ID, userID);
                gameStatusQrCode.putExtra(USER_DISPLAY, userDisplay);
                startActivity(gameStatusQrCode);
            }
        });
    }
}