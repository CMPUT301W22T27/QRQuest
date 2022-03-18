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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReferenceuserScore = db.collection("userScore");
        collectionReferenceuserScore.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> taskout) {
                if (taskout.getResult().exists()) {
                    Object object = new Object();
                    object = taskout.getResult().get("Score:");
                    List<Integer> newScoreList = new ArrayList<Integer>();
                    List<Object> scoreList = Arrays.asList(object);
                    Iterator iterator = scoreList.iterator();
                    while(iterator.hasNext()) {
                        int prevScore = parseInt(String.valueOf(iterator.next()));
                        newScoreList.add(prevScore);
                    }
                    Collections.sort(newScoreList);
                    String min = newScoreList.get(0).toString();
                    String max = newScoreList.get(newScoreList.size()-1).toString();
                    int sum = 0;
                    for(int i = 0; i<newScoreList.size();i++){
                        sum += newScoreList.get(i);
                    }
                    TextView lowestScore = (TextView) findViewById(R.id.textLowestScore);
                    lowestScore.setText("Lowest Score: "+min);
                    TextView highestScore = (TextView) findViewById(R.id.textHighestScore);
                    highestScore.setText("Highest Score: "+max);
                    TextView qrCodeCount = (TextView) findViewById(R.id.textQRCodeCount);
                    qrCodeCount.setText("QR Code Count: "+ newScoreList.size());
                    TextView combinedScore = (TextView) findViewById(R.id.textCombinedScore);
                    combinedScore.setText("Combined Score: "+sum);

                }
                else{
                    TextView lowestScore = (TextView) findViewById(R.id.textLowestScore);
                    lowestScore.setText("Lowest Score: "+0);
                    TextView highestScore = (TextView) findViewById(R.id.textHighestScore);
                    highestScore.setText("Highest Score: "+0);
                    TextView qrCodeCount = (TextView) findViewById(R.id.textQRCodeCount);
                    qrCodeCount.setText("QR Code Count: "+ 0);
                    TextView combinedScore = (TextView) findViewById(R.id.textCombinedScore);
                    combinedScore.setText("Combined Score: "+0);
                }
            }

        });
    }
    private String computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
    }
}