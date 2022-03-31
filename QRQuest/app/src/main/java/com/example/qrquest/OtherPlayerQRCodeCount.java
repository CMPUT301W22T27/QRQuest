package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OtherPlayerQRCodeCount extends AppCompatActivity {
    String username;
    FirebaseFirestore db;
    public ListView qrCodeList;
    public ArrayList<String> qrDataList = new ArrayList<String>();
    public ArrayList<String> qrNameList = new ArrayList<String>();
    public ArrayAdapter<String> qrCodeAdapter;
    TextView otherPlayerUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_player_qrcode_count);
        Intent intent = getIntent();
        username = intent.getStringExtra("User_Name_OtherUserProfile");
        otherPlayerUsername = findViewById(R.id.OtherPlayerUsername);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("UserToQRCode");
        collectionReference.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    List<String> newQrCodeList = new ArrayList<String>();
                    String list = task.getResult().get("QRCode").toString();
                    String[] string = list.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ","")
                            .split(",");


                    otherPlayerUsername.setText(username+"'s QRCodes");
                    for (int i = 0; i < string.length; i++) {
                        qrDataList.add(string[i]);
                    }
                    qrCodeList = findViewById(R.id.OtherPlayQRCodeList);
                    qrCodeAdapter = new ArrayAdapter<String>(OtherPlayerQRCodeCount.this,android.R.layout.simple_list_item_1, qrDataList);
                    qrCodeList.setAdapter(qrCodeAdapter);
                    qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent information = new Intent(OtherPlayerQRCodeCount.this,QRCodeProfile.class);
                            information.putExtra("QRCode_OtherPlayerQRCodeCount", qrDataList.get(i));
                            startActivity(information);
                        }
                    });

                }
            }
        });
    }
}