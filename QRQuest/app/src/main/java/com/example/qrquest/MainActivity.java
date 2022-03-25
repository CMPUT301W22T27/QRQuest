package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

/*
 * Firestore link -> https://console.firebase.google.com/u/2/project/qrquest-b1e1e/firestore/data/~2F
 * I think anybody with this link can view and edit? All new users get stored in the db
 * Still need to implement valid type checking and tests; very naive implementation
 * */
public class MainActivity extends AppCompatActivity {
    Button createAccountButton;
    Button logInButton;
    Button TestButton;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAccountButton = findViewById(R.id.createNewAccountButton);
        TestButton = findViewById(R.id.testButton);
        String username = null;
        String email = null;

        File file = new File(this.getFilesDir(), "login.txt"); // REFERENCE [2]

        try {

            if (file.exists()){
                FileReader fileReader = new FileReader(file);
                char[] buffer = new char[100];

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                email = bufferedReader.readLine();
                Log.i("em", email);
                username = bufferedReader.readLine();
                Log.i("un", username);
            }
            else{

                FileWriter fileWriter = new FileWriter(file.getPath());

                username = UUID.randomUUID().toString(); // REFERENCE: शु-Bham at https://stackoverflow.com/questions/12116092/android-random-string-generator
                email = UUID.randomUUID().toString(); // TODO: check if this username or email already exists (unlikely)
                Log.i("username", username);
                Log.i("email", email);

                db = FirebaseFirestore.getInstance();

                CollectionReference collectionReference = db.collection("Users");
                HashMap<String, String> data = new HashMap<>();
                // add tests for invalid usernames and emails later.
                data.put("Username", username);
                collectionReference.document(email).set(data);

                file.createNewFile(); // Create the login file
                fileWriter.write(email);
                fileWriter.append("\n"+username);
                fileWriter.close();
            }

        } catch (IOException e) {
            Log.e("Error:", "File error");
            // TODO: Error occurred when opening raw file for reading.
        }

        Intent intent = new Intent(this, MainScreen.class);

        intent.putExtra("USER_NAME_MainActivity", username);
        intent.putExtra("EMAIL_ADDRESS_MainActivity", email);
        startActivity(intent);

//        TestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, SideBarActivity.class);
//                startActivity(intent);
//            }
//        });
//        createAccountButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
//                startActivity(intent);
//            }
//        });
    }
}
