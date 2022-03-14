package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;
import com.google.protobuf.Value;

import java.util.HashMap;

/*
* Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
* I think anybody with this link can view and edit? All new users get stored in the db
* Still need to implement valid type checking and tests; very naive implementation
* */

/**
 * Allows user to create account
 */
public class CreateAccount extends AppCompatActivity {

    Button confirmButton;
    Button backButton;
    EditText usernameEditText;
    EditText emailEditText;
    FirebaseFirestore db;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        confirmButton = findViewById(R.id.confirmAccountButton);
        usernameEditText = findViewById(R.id.editTextTextUserName);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        backButton = findViewById(R.id.backToMainPage);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                User newUser = new User(username, email);
                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
                // mDatabase.child("Users").child(email).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    //     @Override
                    //      public void onComplete(@NonNull Task<DataSnapshot> task) {
                        //          if (!task.isSuccessful()) {
                            //             Log.e("firebase", "Error getting data", task.getException());
                            //         }
                        //          else {
                            //             Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            //}
                        //}
                    //           });
                final CollectionReference collectionReference = db.collection("Users");
                collectionReference.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            Log.i("1234",task.getResult().getString("Username"));
                            Toast.makeText(CreateAccount.this, "Account exists", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(CreateAccount.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                            HashMap<String, String> data = new HashMap<>();
                            // add tests for invalid usernames and emails later.
                            data.put("Username", newUser.getUsername());
                            collectionReference.document(email).set(data);
                            Intent intent = new Intent(CreateAccount.this, MainScreen.class);
                            intent.putExtra("USER_NAME_CreateAccount",username);
                            intent.putExtra("EMAIL_ADDRESS_CreateAccount",email);
                            startActivity(intent);
                            usernameEditText.setText("");
                            emailEditText.setText("");
                        }
                    }
                });

//                databaseReference.equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists())
//                            Toast.makeText(CreateAccount.this, "Code exists", Toast.LENGTH_SHORT).show();
//                        else
//                            Log.d("firebase", username);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(CreateAccount.this, MainActivity.class);
                startActivity(backIntent);
            }
        });
    }
}