package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SeeComments extends AppCompatActivity {
    String QRCode;
    TextView qrCode;
    Button addComment;
    Button confirm;
    EditText comment;
    String finalComment;
    ListView commentsTable;
    ArrayList<String> commentList;
    ArrayAdapter<String> commentAdapter;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_comments);
        Intent intent = getIntent();
        QRCode = intent.getStringExtra("QRCode_QRCodeProfile");
        username = intent.getStringExtra("USER_NAME_MainScreen");
        qrCode = findViewById(R.id.qrCodeName);
        qrCode.setText(QRCode);
        commentList = new ArrayList<String>();
        commentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commentList);
        commentsTable = findViewById(R.id.commentsTable);
        commentsTable.setAdapter(commentAdapter);
        confirm = findViewById(R.id.confirm);
        addComment = findViewById(R.id.addComment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addC(view);
            }
        });

    }
    public void addC(View view){
        comment = findViewById(R.id.comment);
        comment.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm(view);
            }
        });



    }
    public void confirm(View view){
        comment = findViewById(R.id.comment);
        finalComment = comment.getText().toString();
        finalComment = username + ": " + finalComment;
        commentList.add(finalComment);
        commentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commentList);
        commentsTable.setAdapter(commentAdapter);
        comment.setVisibility((View.GONE));
        confirm.setVisibility(View.GONE);

    }

}