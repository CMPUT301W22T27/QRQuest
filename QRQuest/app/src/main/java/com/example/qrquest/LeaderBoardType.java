package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LeaderBoardType extends AppCompatActivity {
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board_type);
        Bundle intent = getIntent().getExtras();
        username = intent.getString("USER_NAME_MainScreen");
        Button highestScoreQRCode;
        highestScoreQRCode = findViewById(R.id.HighestScoringQRCodeButton);
        highestScoreQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseHighestScoreQRCode = new Intent(LeaderBoardType.this, HighestScoreQRCodeLB.class);
                chooseHighestScoreQRCode.putExtra("USER_NAME_LeaderBoardType",username);
                startActivity(chooseHighestScoreQRCode);
            }
        });
        Button numberOfQRCodesScanned;
        numberOfQRCodesScanned = findViewById(R.id.NumberOfQRCodesScannedButton);
        numberOfQRCodesScanned.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseNumberOfQRCodesScanned = new Intent(LeaderBoardType.this, NumberOfQRCodesScannedLB.class);
                chooseNumberOfQRCodesScanned.putExtra("USER_NAME_LeaderBoardType",username);
                startActivity(chooseNumberOfQRCodesScanned);
            }
        });
        Button combineScore;
        combineScore = findViewById(R.id.CombinedScoreButton);
        combineScore.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseCombineScore = new Intent(LeaderBoardType.this, CombinedScoreLB.class);
                chooseCombineScore.putExtra("USER_NAME_LeaderBoardType",username);
                startActivity(chooseCombineScore);
            }
        });
    }
}