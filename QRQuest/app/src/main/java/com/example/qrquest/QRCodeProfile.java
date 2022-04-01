package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class QRCodeProfile extends AppCompatActivity {
    Button otherUser;
    String QRCode;
    TextView qrCodeNameBox;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_profile);
        Bundle intent = getIntent().getExtras();
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
        //qrCodeNameBox.setText("Name of the QR Code:"+'\n'+QRCode);
        otherUser = findViewById(R.id.OtherUserButton);
        otherUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent otherUserWithSameQRCode = new Intent(QRCodeProfile.this, OtherUserWithSameQRCode.class);
                otherUserWithSameQRCode.putExtra("QRCode_QRCodeProfile",QRCode);
                startActivity(otherUserWithSameQRCode);
            }
        });
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("QRCodes");
        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String qrCodeName =  task.getResult().get("name").toString();
                    qrCodeNameBox.setText("Name of the QR Code:"+'\n'+qrCodeName);
                }
            }
            });
    }
}