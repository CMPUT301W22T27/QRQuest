package com.example.qrquest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateInfo extends AppCompatActivity {

    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    String oldUsername;
    String oldEmail;
    String newUsername;
    String newEmail;
    EditText nameText;
    EditText emailText;
    Button confirm;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            oldUsername = intent.getString(USER_NAME);
            oldEmail = intent.getString(EMAIL_ADDRESS);
        }

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);

        nameText.setText(oldUsername);
        emailText.setText(oldEmail);

        db = FirebaseFirestore.getInstance();

        confirm = findViewById(R.id.updateConfirm);
        confirm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                newUsername = nameText.getText().toString();
                newEmail = emailText.getText().toString();

                CollectionReference collectionReference = db.collection("Users");
                collectionReference.document(newEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists() && !newEmail.equals(oldEmail)) {
                            Toast.makeText(UpdateInfo.this, "This email account is already in use.", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            HashMap<String, String> data = new HashMap<>();
                            data.put("Username", newUsername);
                            collectionReference.document(newEmail).set(data);

                            collectionReference.document(oldEmail).delete();

                            // Update the user's username in the QRCodeToUser collection
                            CollectionReference QRReference = db.collection("QRCodeToUser");
                            QRReference.whereEqualTo("Username", oldUsername).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    List<DocumentSnapshot> QRCodes = task.getResult().getDocuments();
                                    DocumentReference QRCode;
                                    for (int i = 0; i < QRCodes.size(); i++){
                                        QRCode = QRCodes.get(i).getReference();
                                        QRCode.update("Username", newUsername);
                                    }
                                }
                            });

                            // Update the user's username in the UserToQRCode collection
                            CollectionReference userReference = db.collection("UserToQRCode");
                            userReference.document(oldUsername).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    HashMap<String, ArrayList<String>> data = new HashMap<>();
                                    data.put("QRCode", (ArrayList<String>)task.getResult().get("QRCode"));
                                    userReference.document(newUsername).set(data);

                                    userReference.document(oldUsername).delete();

                                }
                            });

                            // Update the user's username in the userScore collection
                            CollectionReference userScoreReference = db.collection("userScore");
                            userScoreReference.document(oldUsername).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    HashMap<String, ArrayList<Integer>> data = new HashMap<>();
                                    data.put("Score:", (ArrayList<Integer>)task.getResult().get("Score:"));
                                    userScoreReference.document(newUsername).set(data);

                                    userScoreReference.document(oldUsername).delete();

                                }
                            });

                            File file = new File(UpdateInfo.this.getFilesDir(), "login.txt");

                            try {
                                FileWriter fileWriter = new FileWriter(file.getPath());

                                fileWriter.write(newEmail);
                                fileWriter.append("\n"+newUsername);
                                fileWriter.close();

                                Toast.makeText(UpdateInfo.this, "Account updated.", Toast.LENGTH_SHORT).show();

                                Intent mainActivity = new Intent(UpdateInfo.this, MainActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainActivity);

                            } catch (IOException e) {
                                Log.e("Error:", "File error");
                            }



                        }
                    }
                });
            }
        });
    }

}
