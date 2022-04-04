package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/*
 * Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
 * I think anybody with this link can view and edit? All new users get stored in the db
 * Still need to implement valid type checking and tests; very naive implementation
 * */
public class SearchUser extends AppCompatActivity {
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Button search;
        search = findViewById(R.id.SearchOtherUsername);
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                EditText otherUsernameEditText = (EditText) findViewById(R.id.editTextOtherUsername);
                final String username = otherUsernameEditText.getText().toString();
                db = FirebaseFirestore.getInstance();
                final CollectionReference collectionReference = db.collection("userScore");
                collectionReference.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            Intent intent = new Intent(SearchUser.this, OtherUserProfile.class);
                            intent.putExtra("OTHER_USER_NAME",username);
                            startActivity(intent);
                            otherUsernameEditText.setText("");
                        }
                        else{
                            Toast.makeText(SearchUser.this, "Account does not exist or Account does not have submit any QR Code", Toast.LENGTH_SHORT).show();
                            otherUsernameEditText.setText("");
                        }
                    }
                });
            }
        });
    }
}