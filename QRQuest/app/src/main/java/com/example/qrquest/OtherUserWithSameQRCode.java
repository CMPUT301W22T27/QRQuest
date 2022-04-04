package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OtherUserWithSameQRCode extends AppCompatActivity {
    String QRCode;
    FirebaseFirestore db;
    public ListView userList;
    public ArrayList<String> dataList = new ArrayList<String>();
    public ArrayAdapter<String> userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_with_same_qrcode);
        Intent intent = getIntent();
        QRCode = intent.getStringExtra("QRCode_QRCodeProfile");
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("QRCodeToUser");
        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    List<String> newQrCodeList = new ArrayList<String>();
                    String list = task.getResult().get("Username").toString();
                    String[] string = list.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ","")
                            .split(",");
                    for (int i = 0; i < string.length; i++) {
                        dataList.add(string[i]);
                    }

                    userList = findViewById(R.id.OtherUserWithSameQRCodeList);
                    userAdapter = new ArrayAdapter<String>(OtherUserWithSameQRCode.this,android.R.layout.simple_list_item_1,dataList);
                    userList.setAdapter(userAdapter);
                }
            }
        });
    }
}