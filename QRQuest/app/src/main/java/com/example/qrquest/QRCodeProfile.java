package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QRCodeProfile extends AppCompatActivity {
    String username;
    Button otherUser;
    Button seeComments;
    String QRCode;
    TextView qrCodeNameBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_profile);
        Bundle intent = getIntent().getExtras();
        username = intent.getString("USER_NAME_MainScreen");
        if (intent != null){
            if (intent.containsKey("QRCode_TotalNumberQRCodeScanned")){
                QRCode= intent.getString("QRCode_TotalNumberQRCodeScanned");
            }
            else if(intent.containsKey("QRCode_OtherPlayerQRCodeCount")){
                QRCode = intent.getString("QRCode_OtherPlayerQRCodeCount");
            }
            else{
                QRCode = intent.getString("QRCode_GlobalQRCodeList");
            }
        }
        qrCodeNameBox = findViewById(R.id.QRCodeProfileName);
        qrCodeNameBox.setText("Name of the QR Code:"+'\n'+QRCode);
        seeComments = findViewById(R.id.seeComments);
        seeComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent seeComments = new Intent(QRCodeProfile.this, SeeComments.class);
                seeComments.putExtra("QRCode_QRCodeProfile",QRCode);
                seeComments.putExtra("USER_NAME_LeaderBoardType",username);
                startActivity(seeComments);
            }
        });
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