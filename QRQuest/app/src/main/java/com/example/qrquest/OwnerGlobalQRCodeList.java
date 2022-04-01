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
    public ArrayList<String> usernameList = new ArrayList<String>();
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
                                            qrNameList.remove(i);
                                            Log.i("selected",selectedQRCode);
                                            //Remove from QRCodestoUser collection
                                            CollectionReference collectionReferenceQRCodeToUser = db.collection("QRCodeToUser");
                                            collectionReferenceQRCodeToUser.document(selectedQRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().exists()) {
                                                        collectionReferenceQRCodeToUser.document(selectedQRCode).delete();
                                                    }

                                                    // Remove From QRCodes collection
                                                    CollectionReference collectionReferenceQRCodes = db.collection("QRCodes");
                                                    collectionReferenceQRCodes.document(selectedQRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.getResult().exists()) {
                                                                qrScore = task.getResult().get("Score").toString();
                                                                collectionReferenceQRCodes.document(selectedQRCode).delete();
                                                            }

                                                            // Remove from UserToQRCodes
                                                            CollectionReference collectionReferenceUserToQRCode = db.collection("UserToQRCode");
                                                            collectionReferenceUserToQRCode.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();

                                                                        for (int i = 0; i < documentList.size(); i++) {
                                                                            username = documentList.get(i).getId();
                                                                            List<String> qrCodeListAfterDelete = new ArrayList<String>();
                                                                            String list = documentList.get(i).get("QRCode").toString();
                                                                            String[] string = list.replaceAll("\\[", "")
                                                                                    .replaceAll("]", "")
                                                                                    .replaceAll(" ", "")
                                                                                    .split(",");
                                                                            for (int j = 0; j < string.length; j++) {
                                                                                if (string[j].equals(selectedQRCode)) {
                                                                                    usernameList.add(username);
                                                                                    continue;
                                                                                } else {
                                                                                    qrCodeListAfterDelete.add(string[j]);
                                                                                    Log.i("test", Integer.toString(qrCodeListAfterDelete.size()));
                                                                                }
                                                                            }
                                                                            //Log.i("test", qrCodeListAfterDelete.get(0));
                                                                            collectionReferenceUserToQRCode.document(username).delete();
                                                                            HashMap<String, Object> userQRCode = new HashMap<>();
                                                                            userQRCode.put("QRCode", qrCodeListAfterDelete);
                                                                            collectionReferenceUserToQRCode.document(username).set(userQRCode);
                                                                            Log.i("testsize",Integer.toString(usernameList.size()));

                                                                            // Remove score from userScore
                                                                            for (int k=0;k<usernameList.size();k++) {
                                                                                int usernameListIndex = k;
                                                                                CollectionReference collectionReferenceUserScore = db.collection("userScore");
                                                                                collectionReferenceUserScore.document(usernameList.get(k)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if (task.getResult().exists()) {
                                                                                            List<Integer> ScoreListAfterDelete = new ArrayList<Integer>();
                                                                                            String list = task.getResult().get("Score:").toString();
                                                                                            String[] string = list.replaceAll("\\[", "")
                                                                                                    .replaceAll("]", "")
                                                                                                    .replaceAll(" ", "")
                                                                                                    .split(",");
                                                                                            int appear = 0;
                                                                                            for (int i = 0; i < string.length; i++) {
                                                                                                if (string[i].equals(qrScore) && appear == 0) {
                                                                                                    appear += 1;
                                                                                                    continue;
                                                                                                } else if (string[i].equals(qrScore) && appear != 0) {
                                                                                                    ScoreListAfterDelete.add(Integer.valueOf(string[i]));
                                                                                                } else {
                                                                                                    ScoreListAfterDelete.add(Integer.valueOf(string[i]));
                                                                                                }
                                                                                            }
                                                                                            collectionReferenceUserScore.document(usernameList.get(usernameListIndex)).delete();
                                                                                            HashMap<String, Object> userScore = new HashMap<>();
                                                                                            userScore.put("Score:", ScoreListAfterDelete);
                                                                                            collectionReferenceUserScore.document(usernameList.get(usernameListIndex)).set(userScore);
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                    qrCodeAdapter = new ArrayAdapter<String>(OwnerGlobalQRCodeList.this,android.R.layout.simple_list_item_1,qrNameList);
                                                    qrCodeList.setAdapter(qrCodeAdapter);
                                                }
                                            });
                                        }
                                    })

                                    // Remove from userScore

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