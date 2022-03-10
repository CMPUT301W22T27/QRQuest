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

    TextView welcomeMessage;
    Button viewLocButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Intent intent = getIntent();
        String username = intent.getStringExtra(CreateAccount.USER_NAME);
        viewLocButton = findViewById(R.id.viewMapButton);
        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        welcomeMessage.setText("Welcome, " + username + "!");

        viewLocButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainScreen.this, MainScreenActivity.class);
                startActivity(intent);
            }
        });
    }
}