package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

public class QRCodeProfile extends AppCompatActivity {
    Button otherUser;
    String QRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_profile);
        Intent intent = getIntent();
        QRCode = intent.getStringExtra("QRCode_TotalNumberQRCodeScanned");
        otherUser = findViewById(R.id.OtherUserButton);
        otherUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent otherUserWithSameQRCode = new Intent(QRCodeProfile.this, OtherUserWithSameQRCode.class);
                otherUserWithSameQRCode.putExtra("QRCode_QRCodeProfile",QRCode);
                startActivity(otherUserWithSameQRCode);
            }
        });
    }
}