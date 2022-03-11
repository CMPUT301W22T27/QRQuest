package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainScreen extends AppCompatActivity {
    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    TextView welcomeMessage;
    Button viewLocButton;
    Button generateQRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Intent intent = getIntent();
        String username = intent.getStringExtra(CreateAccount.USER_NAME);
        String email = intent.getStringExtra(CreateAccount.EMAIL_ADDRESS);
        viewLocButton = findViewById(R.id.viewMapButton);
        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        welcomeMessage.setText("Welcome, " + username + "!");
        generateQRCode = findViewById(R.id.generateQRCodeButton);
        viewLocButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainScreen.this, MainScreenActivity.class);
                startActivity(intent);
            }
        });
        generateQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseQRCodeType = new Intent(MainScreen.this, ChooseQRCodeType.class);
                chooseQRCodeType.putExtra(USER_NAME, username);
                chooseQRCodeType.putExtra(EMAIL_ADDRESS,email);
                startActivity(chooseQRCodeType);
            }
        });
    }
}