package com.example.qrquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/*
* Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
* I think anybody with this link can view and edit? All new users get stored in the db
* Still need to implement valid type checking and tests; very naive implementation
* */

public class CreateAccount extends AppCompatActivity {

    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    Button confirmButton;
    EditText usernameEditText;
    EditText emailEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        confirmButton = findViewById(R.id.confirmAccountButton);
        usernameEditText = findViewById(R.id.editTextTextUserName);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);

        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String username = usernameEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                User newUser = new User(username, email);
                HashMap<String, String> data = new HashMap<>();
                // add tests for invalid usernames and emails later.
                data.put("Username", newUser.getUsername());
                collectionReference.document(email).set(data);
                Intent intent = new Intent(CreateAccount.this, MainScreen.class);
                intent.putExtra(USER_NAME, username);
                startActivity(intent);
                usernameEditText.setText("");
                emailEditText.setText("");
            }
        });
    }
}