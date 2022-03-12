package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChooseQRCodeType extends AppCompatActivity {
    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    Button generateLoginCode;
    Button generateGameStatusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_qrcode_type);
        Intent intent = getIntent();
        generateLoginCode = findViewById(R.id.loginCodeButton);
        generateGameStatusCode = findViewById(R.id.gameStatusCodeButton);
        String username = intent.getStringExtra(MainScreen.USER_NAME);
        String email = intent.getStringExtra(MainScreen.EMAIL_ADDRESS);

        generateLoginCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent loginQrCode = new Intent(ChooseQRCodeType.this, LoginQRCode.class);
                loginQrCode.putExtra(USER_NAME, username);
                loginQrCode.putExtra(EMAIL_ADDRESS,email);
                startActivity(loginQrCode);
            }
        });

        generateGameStatusCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent gameStatusQrCode = new Intent(ChooseQRCodeType.this, GameStatusQRCode.class);
                gameStatusQrCode.putExtra(USER_NAME, username);
                gameStatusQrCode.putExtra(EMAIL_ADDRESS,email);
                startActivity(gameStatusQrCode);
            }
        });
    }
}