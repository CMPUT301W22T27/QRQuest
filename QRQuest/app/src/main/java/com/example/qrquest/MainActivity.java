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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
        logInButton = findViewById(R.id.loginButton);
        TestButton = findViewById(R.id.testButton);

        TestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SideBarActivity.class);
                startActivity(intent);
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scanner scanner = new Scanner(view, MainActivity.this);
                scanner.startScan();
            }
        });
    }
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            QRCode qrCode = new QRCode(intentResult.getContents(), true);
            db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection("LoginQRCodes:");
            Log.i("Unhashed:", intentResult.getContents());
//            Log.i("Hashed:",  qrCode.getHash());
            collectionReference.document(qrCode.getHash()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> taskout) {
                    if (taskout.getResult().exists()) {
                        Intent intent = new Intent(MainActivity.this, MainScreen.class);
                        final CollectionReference collectionReferencein = db.collection("Users");
                        String email = taskout.getResult().getString("Email");
                        collectionReferencein.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> taskin) {
                                String username = taskin.getResult().getString("Username");
                                intent.putExtra("USER_NAME_MainActivity", username);
                                intent.putExtra("EMAIL_ADDRESS_MainActivity", email);
                                startActivity(intent);
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No Existing QR Code", Toast.LENGTH_SHORT).show();
                        }
                    }

            });
        } else {
            Toast.makeText(getApplicationContext(), "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }


    }
}

