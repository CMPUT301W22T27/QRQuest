package com.example.qrquest;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
 * I think anybody with this link can view and edit? All new users get stored in the db
 * Still need to implement valid type checking and tests; very naive implementation
 * */

public class HighestScoreQRCodeLB extends AppCompatActivity {
    public ListView qrCodeList;
    public ArrayList<String> dataList;
    public ArrayAdapter<String> qrCodeAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highest_score_qrcode_lb);
        Bundle intent = getIntent().getExtras();
        String playerUsername = intent.getString("USER_NAME_MainScreen");
        String[] leaderList = {};
        List<String> username = new ArrayList<String>();
        List<Integer> scoreList = new ArrayList<Integer>();
        db = FirebaseFirestore.getInstance();
        dataList = new ArrayList<String>();
        Map<Integer, String> data = new HashMap<>();
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
                        collectionReference.document(username.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    Collections.sort(newScoreList);
                                    int max = newScoreList.get(newScoreList.size() - 1);
                                    scoreList.add(max);

                                    data.put(max, username.get(index));

                                } else {
                                    Log.i("test", "fail");
                                }
                                //Print our the List
                                if (scoreList.size() == maxSize) {
                                    Collections.sort(scoreList, Collections.reverseOrder());
                                    for (int i = 0; i < scoreList.size(); i++) {
                                        TextView playerRank = findViewById(R.id.YourRank);
                                        if (username.contains(playerUsername)) {
                                            if (data.get(scoreList.get(i)).equals(playerUsername)) {
                                                playerRank.setText("Your Rank is : " + i + 1);
                                            }
                                        }
                                        else{
                                            playerRank.setText("You don't have a rank!");
                                        }
                                        int usernameLength = data.get(scoreList.get(i)).length();
                                        int gap = 30-usernameLength;
                                        String whiteSpace = "";
                                        for (int j=0;j<gap;j++){
                                            whiteSpace+= new String(" ");
                                        }
                                        dataList.add(Integer.toString(i + 1) + "                    " + data.get(scoreList.get(i)) + whiteSpace +  + scoreList.get(i));
                                    }
                                    qrCodeList = findViewById(R.id.QRcodeHighestScoreList);
                                    qrCodeAdapter = new ArrayAdapter<String>(HighestScoreQRCodeLB.this, R.layout.support_simple_spinner_dropdown_item, dataList);
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