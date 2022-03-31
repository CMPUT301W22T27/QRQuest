package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GlobalQRCodeList extends AppCompatActivity {
    FirebaseFirestore db;
    public ListView qrCodeList;
    public ArrayList<String> qrDataList = new ArrayList<String>();
    public ArrayList<String> qrNameList = new ArrayList<String>();
    public ArrayAdapter<String> qrCodeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_qrcode_list);
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("QRCodes");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                    for (int i=0;i < documentList.size();i++) {
                        int index = i;
                        qrDataList.add(documentList.get(i).getId());
                        qrNameList.add(documentList.get(i).getString("name"));
                    }
                    qrCodeList = findViewById(R.id.GlobalQRCodeList);
//                    qrCodeAdapter = new ArrayAdapter<String>(GlobalQRCodeList.this,android.R.layout.simple_list_item_1, qrDataList);
                    qrCodeAdapter = new ArrayAdapter<String>(GlobalQRCodeList.this,android.R.layout.simple_list_item_1, qrNameList);
                    qrCodeList.setAdapter(qrCodeAdapter);
                    qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String qrCodeHash = qrDataList.get(i);
                            String qrCodeName = qrNameList.get(i);
                            Intent qrCodeProfile = new Intent (GlobalQRCodeList.this,QRCodeProfile.class);
                            qrCodeProfile.putExtra("QRCode_GlobalQRCodeList",qrCodeHash);
                            startActivity(qrCodeProfile);
                        }
                    });
                }
            }
        });
    }
}