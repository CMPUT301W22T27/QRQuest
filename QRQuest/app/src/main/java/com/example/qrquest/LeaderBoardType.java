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
                Intent chooseLeaderBoardType = new Intent(LeaderBoardType.this, HighestScoreQRCodeLB.class);
                chooseLeaderBoardType.putExtra("USER_NAME_LeaderBoardType",username);
                startActivity(chooseLeaderBoardType);
            }
        });
    }
}