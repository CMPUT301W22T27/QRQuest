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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OwnerGlobalQRCodeList extends AppCompatActivity {
    FirebaseFirestore db;
    public ListView qrCodeList;
    public ArrayList<String> qrDataList = new ArrayList<String>();
    public ArrayList<String> qrNameList = new ArrayList<String>();
    public ArrayAdapter<String> qrCodeAdapter;
    public String qrHash;
    public String qrScore;
    public String qrname;
    public String selectedQRCode;
    public String username;
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
                    for (int i = 0; i < documentList.size(); i++) {
                        int index = i;
                        qrHash = documentList.get(i).getId();
                        qrname = documentList.get(i).getString("name");
                        qrDataList.add(qrHash);
                        qrNameList.add(qrname);
                    }
                    qrCodeList = findViewById(R.id.GlobalQRCodeList);
//                    qrCodeAdapter = new ArrayAdapter<String>(GlobalQRCodeList.this,android.R.layout.simple_list_item_1, qrDataList);
                    qrCodeAdapter = new ArrayAdapter<String>(OwnerGlobalQRCodeList.this, android.R.layout.simple_list_item_1, qrNameList);
                    qrCodeList.setAdapter(qrCodeAdapter);
                    qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            new AlertDialog.Builder(OwnerGlobalQRCodeList.this)
                                    .setTitle("Choose")
                                    .setMessage("Do you want to delete this QR Code?")

                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int j) {
                                            selectedQRCode = qrDataList.get(i);
                                            qrDataList.remove(i);
                                            Log.i("selected",selectedQRCode);
                                            //Remove from QRCodestoUser collection
                                            CollectionReference collectionReferenceQRCodeToUser = db.collection("QRCodeToUser");
                                            collectionReferenceQRCodeToUser.document(selectedQRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().exists()) {
                                                        //collectionReferenceQRCodeToUser.document(selectedQRCode).delete();
                                                    }

                                                    // Remove From QRCodes collection
                                                    CollectionReference collectionReferenceQRCodes = db.collection("QRCodes");
                                                    collectionReferenceQRCodes.document(selectedQRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.getResult().exists()) {
                                                                qrScore = task.getResult().get("Score").toString();
                                                                //collectionReferenceQRCodes.document(selectedQRCode).delete();
                                                            }

                                                            // Remove from UserToQRCodes
                                                            CollectionReference collectionReferenceUserToQRCode = db.collection("UserToQRCode");
                                                            collectionReferenceUserToQRCode.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();

                                                                        for (int i = 0; i < documentList.size(); i++) {
                                                                            String usernameInUserToQRCode = documentList.get(i).getId();
                                                                            Log.i("username",usernameInUserToQRCode);
                                                                            List<String> qrCodeListAfterDelete = new ArrayList<String>();
                                                                            String list = documentList.get(i).get("QRCode").toString();
                                                                            Log.i("test",list);
                                                                            String[] string = list.replaceAll("\\[", "")
                                                                                    .replaceAll("]", "")
                                                                                    .replaceAll(" ", "")
                                                                                    .split(",");
                                                                            for (int j = 0; j < string.length; j++) {
                                                                                if (string[j].equals(selectedQRCode)) {
                                                                                    continue;
                                                                                }
                                                                                else{
                                                                                    qrCodeListAfterDelete.add(string[j]);
                                                                                    Log.i("test",Integer.toString(qrCodeListAfterDelete.size()));
                                                                                }
                                                                            }
                                                                            Log.i("test",qrCodeListAfterDelete.get(0));
                                                                            collectionReferenceUserToQRCode.document(usernameInUserToQRCode).delete();
                                                                            HashMap<String, Object> userQRCode = new HashMap<>();
                                                                            userQRCode.put("QRCode", qrCodeListAfterDelete);
                                                                            collectionReferenceUserToQRCode.document(usernameInUserToQRCode).set(userQRCode);
                                                                            }
                                                                        }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    })

                                    // Remove from userScore

                                    //qrCodeAdapter = new ArrayAdapter<String>(OwnerGlobalQRCodeList.this,android.R.layout.simple_list_item_1,qrDataList);
                                    //qrCodeList.setAdapter(qrCodeAdapter);

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int k) {
                                            return;
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    });
                }
            }
        });
    }
}