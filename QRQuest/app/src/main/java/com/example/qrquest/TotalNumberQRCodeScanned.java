package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TotalNumberQRCodeScanned extends AppCompatActivity {
    TextView title;
    FirebaseFirestore db;
    public ListView qrCodeList;
    public ArrayList<String> dataList = new ArrayList<String>();
    public ArrayAdapter<String> qrCodeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_number_qrcode_scanned);
        Intent intent = getIntent();
        String username = intent.getStringExtra("User_Name_GameStatusCode");
        title = findViewById(R.id.totalNumberQRCodeScannedTextView);
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


                    title.setText("  You've Scanned "+ '\n' + "           "+string.length +'\n' +"     "+"QR Codes" );
                    for (int i = 0; i < string.length; i++) {
                        dataList.add(string[i]);
                    }

                    qrCodeList = findViewById(R.id.TotalNumberQRcodeScannedList);
                    qrCodeAdapter = new ArrayAdapter<String>(TotalNumberQRCodeScanned.this,android.R.layout.simple_list_item_1,dataList);
                    qrCodeList.setAdapter(qrCodeAdapter);
                    qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            new AlertDialog.Builder(TotalNumberQRCodeScanned.this)
                                    .setTitle("Choose")
                                    .setMessage("What do you want to do with this QR Code?")

                                    .setPositiveButton("Information", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int j) {
                                            Intent information = new Intent(TotalNumberQRCodeScanned.this,QRCodeProfile.class);
                                            information.putExtra("QRCode_TotalNumberQRCodeScanned",dataList.get(i));
                                            startActivity(information);
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton("Delete", new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int k) {
                                            dataList.remove(i);
                                            List<String> qrCodeListAfterDelete = new ArrayList<String>();
                                            title.setText("  You've Scanned "+ '\n' + "           "+dataList.size()+'\n' +"     "+"QR Codes" );
                                            for (int index = 0; index < dataList.size(); index++) {
                                                qrCodeListAfterDelete.add(dataList.get(index));
                                            }
                                            collectionReference.document(username).delete();
                                            HashMap<String, Object> userQRCode = new HashMap<>();
                                            userQRCode.put("QRCode", qrCodeListAfterDelete);
                                            collectionReference.document(username).set(userQRCode);
                                            qrCodeAdapter = new ArrayAdapter<String>(TotalNumberQRCodeScanned.this,android.R.layout.simple_list_item_1,dataList);
                                            qrCodeList.setAdapter(qrCodeAdapter);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    } );

                }
                else {
                    title.setText("  You've Scanned "+ '\n' + "           "+0 +'\n' +"     "+"QR Codes" );
                }
            }
        });
    }
}