package com.example.qrquest;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

/*
 * Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
 * I think anybody with this link can view and edit? All new users get stored in the db
 * Still need to implement valid type checking and tests; very naive implementation
 * */

public class NumberOfQRCodesScannedLB extends AppCompatActivity {
    public ListView qrCodeList;
    public ArrayList<Data> dataList;
    public ArrayAdapter<Data> qrCodeAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_of_qrcodes_scanned_lb);
        Bundle intent = getIntent().getExtras();
        String playerUsername = intent.getString("USER_NAME_LeaderBoardType");
        String[] leaderList = {};
        List<String> username = new ArrayList<String>();
        List<Integer> scoreList = new ArrayList<Integer>();
        db = FirebaseFirestore.getInstance();
        dataList = new ArrayList<Data>();
        Map<String, Integer> data = new HashMap<>();
        //Get all the username in the userScore collection
        final CollectionReference collectionReference = db.collection("userScore");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                    int maxSize = documentList.size();
                    for (int i=0;i < documentList.size();i++){
                        int index = i;
                        username.add(documentList.get(i).getId());
                        //Get the max score for each username
                        collectionReference.document(username.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> taskin) {
                                if (taskin.getResult().exists()) {

                                    List<Integer> newScoreList = new ArrayList<Integer>();
                                    String list = taskin.getResult().get("Score:").toString();
                                    String[] string = list.replaceAll("\\[", "")
                                            .replaceAll("]", "")
                                            .replaceAll(" ", "")
                                            .split(",");
                                    for (int i = 0; i < string.length; i++) {
                                        newScoreList.add(Integer.valueOf(string[i]));
                                    }
                                    int size = newScoreList.size();
                                    scoreList.add(size);

                                    data.put(username.get(index),size);

                                } else {
                                    Log.i("test", "fail");
                                }
                                //Print out the List
                                if (scoreList.size() == maxSize) {
                                    List<Map.Entry<String,Integer>> list = new ArrayList<>(data.entrySet());
                                    list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
                                    int index = 0;
                                    for (Map.Entry<String, Integer> entry : list) {
                                        index +=1;

                                        String key = entry.getKey();
                                        Integer value = entry.getValue();


                                        TextView playerRank = findViewById(R.id.YourRankNumberOfQRCodesScanned);
                                        if (username.contains(playerUsername)) {

                                            if (key.equals(playerUsername)) {

                                                playerRank.setText("Your Rank is : " + Integer.toString(index));
                                            }
                                        }
                                        else{
                                            playerRank.setText("You don't have a rank!");
                                        }
                                        int usernameLength = key.length();
                                        if (usernameLength >10){
                                            String shortUsername = key.substring(0,10)+"...";
                                            key = shortUsername;
                                        }
                                        dataList.add(new Data(Integer.toString(index),key,Integer.toString(value)));
                                    }
                                    qrCodeList = findViewById(R.id.QRcodeScannedList);
                                    qrCodeAdapter = new CustomList(NumberOfQRCodesScannedLB.this,dataList);
                                    qrCodeList.setAdapter(qrCodeAdapter);

                                    return;
                                }
                            }

                        });
                    }

                }
                else{
                    Log.i("test","fail");
                }

            }
        });


    }
}