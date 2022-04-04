package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class OwnerGlobalPlayerList extends AppCompatActivity {
    FirebaseFirestore db;
    public ListView qrCodeList;
    public ArrayList<String> userEmailList = new ArrayList<String>();
    public ArrayList<String> usernameList = new ArrayList<String>();
    public ArrayAdapter<String> qrCodeAdapter;
    public String userEmail;
    public String qrScore;
    public String username;
    public String selectedUser;
    public String selectedEmail;
    public String gameStatusQRCode;
    public String loginQRCode;
   // public String username_gameStatusQRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_global_player_list);
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                    for (int i = 0; i < documentList.size(); i++) {
                        int index = i;
                        userEmail = documentList.get(i).getId();
                        //Log.i("email",userEmail);

                        username = documentList.get(i).getString("Username");
                        //Log.i("name",username);
                        userEmailList.add(userEmail);
                        usernameList.add(username);
                    }
                    qrCodeList = findViewById(R.id.OwnerGlobalPlayerList);
//                    qrCodeAdapter = new ArrayAdapter<String>(GlobalQRCodeList.this,android.R.layout.simple_list_item_1, qrDataList);
                    qrCodeAdapter = new ArrayAdapter<String>(OwnerGlobalPlayerList.this, android.R.layout.simple_list_item_1, usernameList);
                    qrCodeList.setAdapter(qrCodeAdapter);
                    qrCodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            new AlertDialog.Builder(OwnerGlobalPlayerList.this)
                                    .setTitle("Choose")
                                    .setMessage("Do you want to delete this Player?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int j) {
                                            selectedUser = usernameList.get(i);
                                            selectedEmail = userEmailList.get(i);
                                            usernameList.remove(i);
                                            Log.i("selected",selectedUser);
                                            //Remove from UserToQRCode collection
                                            CollectionReference collectionReferenceUserToQRCode = db.collection("UserToQRCode");
                                            collectionReferenceUserToQRCode.document(selectedUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().exists()) {
                                                        collectionReferenceUserToQRCode.document(selectedUser).delete();

                                                    }

                                                    // Remove From GameStatusQRCode collection
                                                    CollectionReference collectionReferenceGameStatusQRCode = db.collection("GameStatusQRCodes:");
                                                    collectionReferenceGameStatusQRCode.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                                                                for (int i = 0; i < documentList.size(); i++) {
                                                                    gameStatusQRCode = documentList.get(i).getId();
                                                                    String username_gameStatusQRCode = documentList.get(i).getString("UserName");
                                                                    Log.i("username",gameStatusQRCode);
                                                                    if (username_gameStatusQRCode.equals(selectedUser)) {
                                                                        collectionReferenceGameStatusQRCode.document(gameStatusQRCode).delete();
                                                                    }
                                                                }
                                                            }

                                                            // Remove from LoginQRCodes
                                                            CollectionReference collectionReferenceLoginQRCode = db.collection("LoginQRCodes:");
                                                            collectionReferenceLoginQRCode.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                                                                        for (int i = 0; i < documentList.size(); i++) {
                                                                            loginQRCode = documentList.get(i).getId();
                                                                            String email_loginQRCode = documentList.get(i).getString("Email");
                                                                            if (email_loginQRCode.equals(selectedEmail)) {
                                                                                collectionReferenceLoginQRCode.document(loginQRCode).delete();
                                                                            }
                                                                        }
                                                                    }

                                                                    // Remove score from QRCodeToUser
                                                                    CollectionReference collectionReferenceQRCodeToUser = db.collection("QRCodeToUser");
                                                                    collectionReferenceQRCodeToUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                ArrayList<String> qrCodeListQRCodeToUser = new ArrayList<String>();
                                                                                List<String> usernameListAfterDelete = new ArrayList<String>();
                                                                                for (int i = 0; i < documentList.size(); i++) {
                                                                                    String QRCode = documentList.get(i).getId();
                                                                                    String list = documentList.get(i).getString("Username");
                                                                                    String[] string = list.replaceAll("\\[", "")
                                                                                            .replaceAll("]", "")
                                                                                            .replaceAll(" ", "")
                                                                                            .split(",");
                                                                                    for (int j = 0; j < string.length; j++) {
                                                                                        if (string[j].equals(selectedUser)) {
                                                                                            qrCodeListQRCodeToUser.add(QRCode);
                                                                                            continue;
                                                                                        } else {
                                                                                            usernameListAfterDelete.add(string[j]);
                                                                                        }
                                                                                    }
                                                                                    //Log.i("test", qrCodeListAfterDelete.get(0));
                                                                                    collectionReferenceQRCodeToUser.document(QRCode).delete();
                                                                                    HashMap<String, Object> qrCodeUser = new HashMap<>();
                                                                                    qrCodeUser.put("Username", usernameListAfterDelete);
                                                                                    collectionReferenceQRCodeToUser.document(QRCode).set(qrCodeUser);
                                                                                    //Log.i("qtu","success");
                                                                                }
                                                                            }
                                                                            // Remove from Users collection
                                                                            CollectionReference collectionReferenceUsers = db.collection("Users");
                                                                            collectionReferenceUsers.document(selectedEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.getResult().exists()) {
                                                                                        collectionReferenceUsers.document(selectedEmail).delete();

                                                                                    }
                                                                                    //Remove from userScore
                                                                                    CollectionReference collectionReferenceuserScore = db.collection("userScore");
                                                                                    collectionReferenceuserScore.document(selectedUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                            if (task.getResult().exists()) {
                                                                                                collectionReferenceuserScore.document(selectedUser).delete();
                                                                                                //Log.i("score","success");
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                qrCodeAdapter = new ArrayAdapter<String>(OwnerGlobalPlayerList.this,android.R.layout.simple_list_item_1,usernameList);
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