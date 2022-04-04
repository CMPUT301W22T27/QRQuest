package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Seecomments extends AppCompatActivity {
    String QRCode;
    TextView qrCode;
    Button addComment;
    Button confirm;
    EditText comment;
    ArrayList<String> comments;
    String finalComment;
    ListView commentsTable;
    ArrayList<String> commentList;
    ArrayAdapter<String> commentAdapter;
    String username;
    String email;
    FirebaseFirestore db;
    CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seecomments);
        Intent intent = getIntent();
        db = FirebaseFirestore.getInstance();
        QRCode = intent.getStringExtra("QRCode_QRCodeProfile");
        //username = intent.getStringExtra("USER_NAME_MainScreen");
        qrCode = findViewById(R.id.qrCodeName);
        qrCode.setText(QRCode.substring(0, 12));

        File file = new File(this.getFilesDir(), "login.txt"); // REFERENCE [2]

        try {

            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                char[] buffer = new char[100];

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                email = bufferedReader.readLine();
                Log.i("em", email);
                username = bufferedReader.readLine();
                Log.i("un", username);
            } else {

                FileWriter fileWriter = new FileWriter(file.getPath());

                username = UUID.randomUUID().toString(); // REFERENCE: शु-Bham at https://stackoverflow.com/questions/12116092/android-random-string-generator
                email = UUID.randomUUID().toString(); // TODO: check if this username or email already exists (unlikely)
                Log.i("username", username);
                Log.i("email", email);
            }

        } catch (IOException e) {
            Log.e("Error:", "File error");
            // TODO: Error occurred when opening raw file for reading.
        }


                collectionReference = db.collection("QRCodes");
        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    if (task.getResult().contains("Comments")){
                        commentList = (ArrayList<String>)task.getResult().get("Comments"); // TODO: check this cast
                    }
                    else {
                        commentList = new ArrayList<String>();
                    }

                    commentAdapter = new ArrayAdapter<String>(Seecomments.this, android.R.layout.simple_list_item_1, commentList);
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
                else{
                    Toast.makeText(Seecomments.this, "QRCode does not exist", Toast.LENGTH_SHORT).show();
                }
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
        finalComment = username.substring(0, 8) + ": " + finalComment;
        commentList.add(finalComment);
        commentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commentList);
        commentsTable.setAdapter(commentAdapter);
        comment.setVisibility((View.GONE));
        confirm.setVisibility(View.GONE);

        collectionReference.document(QRCode).update("Comments", commentList);

//        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.getResult().exists()) {
//
//                    HashMap<String, ArrayList<String>> data = new HashMap<>();
//                    data.put("Comments", commentList);
//                    collectionReference.document(QRCode).set(data);
//                    Toast.makeText(SeeComments.this, "Comment uploaded", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(SeeComments.this, "QRCode does not exist", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

}