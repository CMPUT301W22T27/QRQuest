package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ScanSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
        Bundle extras = getIntent().getExtras();
        String score = extras.getString("score");
        TextView scoreDisplay = findViewById(R.id.scoreView);
        scoreDisplay.setText(score);



        }

}