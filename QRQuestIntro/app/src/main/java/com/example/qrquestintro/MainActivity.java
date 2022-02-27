package com.example.qrquestintro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button createAccount;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if Create Account button is clicked, go to createAccount class
        createAccount = findViewById(R.id.createAccount);
        //Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
        login = findViewById(R.id.LoginWithQRCode);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(createAccountIntent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to Camera Class
            }
        });
    }
}

