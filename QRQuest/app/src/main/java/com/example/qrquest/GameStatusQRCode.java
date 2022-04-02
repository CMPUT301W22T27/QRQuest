package com.example.qrquest;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import java.util.*;
import java.util.stream.*;

/**
 * GameStatusQRCode
 * Extends AppCompatActivity
 */
public class GameStatusQRCode extends AppCompatActivity {
    public ImageView GameStatusQRCodeImage;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    FirebaseFirestore db;
    TextView lowestScore;
    TextView highestScore;
    TextView qrCodeCount;
    TextView combinedScore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_status_qrcode);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChooseQRCodeType.USER_NAME);
        String emailAddress = intent.getStringExtra(ChooseQRCodeType.EMAIL_ADDRESS);
        TextView textUsername = findViewById(R.id.gameStatusUsername);
        textUsername.setText("Username: "+ username);

        //Create QR Code
        GameStatusQRCodeImage = findViewById(R.id.GameStatusQRCode);
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        String hash = computeHash(username);
        qrgEncoder = new QRGEncoder(hash, null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            GameStatusQRCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("GameStatusQRCodes:");
        HashMap<String, String> data = new HashMap<>();
        // add tests for invalid usernames and emails later.
        data.put("UserName", username);
        collectionReference.document(hash).set(data);

        final CollectionReference collectionReferenceuserScore = db.collection("userScore");
        collectionReferenceuserScore.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    List<Integer> newScoreList = new ArrayList<Integer>();
                    String list = task.getResult().get("Score:").toString();
                    String[] string = list.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .replaceAll(" ","")
                            .split(",");
                    for (int i = 0; i < string.length; i++) {
                        newScoreList.add(Integer.valueOf(string[i]));
                    }
                    Collections.sort(newScoreList);
                    String min = newScoreList.get(0).toString();
                    String max = newScoreList.get(newScoreList.size()-1).toString();
                    int sum = 0;
                    for(int i = 0; i<newScoreList.size();i++){
                        sum += newScoreList.get(i);
                    }
                    lowestScore = (TextView) findViewById(R.id.textLowestScore);
                    lowestScore.setText("Lowest Score: "+min);
                    highestScore = (TextView) findViewById(R.id.textHighestScore);
                    highestScore.setText("Highest Score: "+max);
                    qrCodeCount = (TextView) findViewById(R.id.textQRCodeCount);
                    qrCodeCount.setText("QR Code Count: "+ newScoreList.size());
                    combinedScore = (TextView) findViewById(R.id.textCombinedScore);
                    combinedScore.setText("Combined Score: "+sum);

                }
                else{
                    lowestScore = (TextView) findViewById(R.id.textLowestScore);
                    lowestScore.setText("Lowest Score: "+0);
                    highestScore = (TextView) findViewById(R.id.textHighestScore);
                    highestScore.setText("Highest Score: "+0);
                    qrCodeCount = (TextView) findViewById(R.id.textQRCodeCount);
                    qrCodeCount.setText("QR Code Count: "+ 0);
                    combinedScore = (TextView) findViewById(R.id.textCombinedScore);
                    combinedScore.setText("Combined Score: "+0);
                }
                qrCodeCount.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Intent totalNumberQRCodeScanned = new Intent(GameStatusQRCode.this,TotalNumberQRCodeScanned.class);
                        totalNumberQRCodeScanned.putExtra("User_Name_GameStatusCode",username);
                        startActivity(totalNumberQRCodeScanned);
                    }
                });
            }

        });
    }
    private String computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
    }
    public void onClick(View view){
        TextView lowestScore = findViewById(R.id.textLowestScore);
        lowestScore.setText("123456789");
    }
}