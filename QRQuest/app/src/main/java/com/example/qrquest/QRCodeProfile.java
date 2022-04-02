package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class QRCodeProfile extends AppCompatActivity {
    Button otherUser;
    String QRCode;
    TextView qrCodeNameBox;
    TextView qrScore;
    FirebaseFirestore db;
    ImageView qrItemImage;
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
        qrItemImage = findViewById(R.id.ItemImageView);
        qrScore = findViewById(R.id.ScoreDisplay);
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
                    String encodedImage = task.getResult().get("Image").toString();
                    String score = task.getResult().get("Score").toString();
                    // converting the image back to a bitmap from a string

                    if (encodedImage != "") {
                        byte[] decodedArray = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap image = BitmapFactory.decodeByteArray(decodedArray, 0, decodedArray.length);
                        qrItemImage.setImageBitmap(image);
                    }

                    qrCodeNameBox.setText("Name of the QR Code:"+'\n'+qrCodeName);
                    qrScore.setText("Score: " + score);

                }
            }
            });
    }


}