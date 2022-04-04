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

public class OtherUserProfile extends AppCompatActivity {
    FirebaseFirestore db;
    TextView lowestScore;
    TextView highestScore;
    TextView qrCodeCount;
    TextView combinedScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Bundle intent = getIntent().getExtras();
        String username = intent.getString("OTHER_USER_NAME");
        TextView TitleTextView;
        TextView UsernameTextView;
        TitleTextView = findViewById(R.id.OtherUserTitle);
        UsernameTextView = findViewById(R.id.otherUserName);
        lowestScore = findViewById(R.id.otherUserLowestScore);
        highestScore = findViewById(R.id.otherUserHighestScore);
        qrCodeCount = findViewById(R.id.otherUserQRCodeCount);
        combinedScore =  findViewById(R.id.otherUserCombinedScore);
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
                    lowestScore.setText("Lowest Score: "+min);
                    highestScore.setText("Highest Score: "+max);

                    qrCodeCount.setText("QR Code Count: "+ newScoreList.size());
                    combinedScore.setText("Combined Score: "+sum);
                }
                else{

                    lowestScore.setText("Lowest Score: "+0);
                    highestScore.setText("Highest Score: "+0);
                    qrCodeCount.setText("QR Code Count: "+ 0);
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
        qrCodeCount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent otherUserQRCodeCount = new Intent(OtherUserProfile.this,OtherPlayerQRCodeCount.class);
                otherUserQRCodeCount.putExtra("User_Name_OtherUserProfile",username);
                startActivity(otherUserQRCodeCount);
            }
        });

    }
}