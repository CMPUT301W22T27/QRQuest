package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
 * I think anybody with this link can view and edit? All new users get stored in the db
 * Still need to implement valid type checking and tests; very naive implementation
 * */
public class OtherUserProfile extends AppCompatActivity {
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Bundle intent = getIntent().getExtras();
        String username = intent.getString("OTHER_USER_NAME");
        TextView TitleTextView;
        TextView UserHighestScoreTextView;
        TextView UserLowestScoreTextView;
        TextView UserQRCodeCountTextView;
        TextView UserCombinedScoreTextView;
        TextView UsernameTextView;
        TitleTextView = findViewById(R.id.OtherUserTitle);
        UserHighestScoreTextView = findViewById(R.id.otherUserHighestScore);
        UserLowestScoreTextView = findViewById(R.id.otherUserLowestScore);
        UserQRCodeCountTextView = findViewById(R.id.otherUserQRCodeCount);
        UserCombinedScoreTextView = findViewById(R.id.otherUserCombinedScore);
        UsernameTextView = findViewById(R.id.otherUserName);
        TitleTextView.setText(username+"'s Profile:");
        UsernameTextView.setText("UserName: " + username);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReferenceuserScore = db.collection("userScore");
        collectionReferenceuserScore.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    List<Integer> newScoreList = new ArrayList<Integer>();
                    String list = task.getResult().get("Score:").toString();
                    String[] string = list.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ","")
                            .split(",");
                    for (int i = 0; i < string.length; i++) {
                        newScoreList.add(Integer.valueOf(string[i]));
                    }
                    Collections.sort(newScoreList);
                    String min = newScoreList.get(0).toString();
                    String max = newScoreList.get(newScoreList.size()-1).toString();
                    int sum = 0;
                    for(int i = 0; i<newScoreList.size();i++){
                        sum += newScoreList.get(i);
                    }
                    UserLowestScoreTextView.setText("Lowest Score: "+min);
                    UserHighestScoreTextView.setText("Highest Score: "+max);
                    UserQRCodeCountTextView.setText("QR Code Count: "+ newScoreList.size());
                    UserCombinedScoreTextView.setText("Combined Score: "+sum);

                }
                else{
                    TextView lowestScore = (TextView) findViewById(R.id.textLowestScore);
                    lowestScore.setText("Lowest Score: "+0);
                    TextView highestScore = (TextView) findViewById(R.id.textHighestScore);
                    highestScore.setText("Highest Score: "+0);
                    TextView qrCodeCount = (TextView) findViewById(R.id.textQRCodeCount);
                    qrCodeCount.setText("QR Code Count: "+ 0);
                    TextView combinedScore = (TextView) findViewById(R.id.textCombinedScore);
                    combinedScore.setText("Combined Score: "+0);
                }
            }

        });
        Button returnToSearch;
        returnToSearch = findViewById(R.id.Return);
        returnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this, SearchUser.class);
                startActivity(intent);
            }
        });
    }
}