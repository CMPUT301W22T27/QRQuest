package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TotalNumberQRCodeScanned extends AppCompatActivity {
    TextView title;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_number_qrcode_scanned);
        Intent intent = getIntent();
        String username = intent.getStringExtra("User_Name_GameStatusCode");
        title = findViewById(R.id.totalNumberQRCodeScannedTextView);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("GameStatusQRCodes:");
    }
}