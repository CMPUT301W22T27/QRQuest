package com.example.qrquest;


import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.client.android.Intents;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback{
    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    TextView welcomeMessage;
    boolean isPermissionGranted;
    MapView mapView;
    Button generateQRCode;
    String username;
    String email;
    Button subCodeButton;
    Button search;
    Button leaderBoard;
    Button globalQRCodeList;
    Button deleteCode;
    Button deletePlayer;
    String score;
    String qrCodeHash;
    FirebaseFirestore dbOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (intent.containsKey("USER_NAME_MainActivity")){
                username = intent.getString("USER_NAME_MainActivity");
                email = intent.getString("EMAIL_ADDRESS_MainActivity");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username + "!");
            }
            else{
                username = intent.getString("USER_NAME_CreateAccount");
                email = intent.getString("EMAIL_ADDRESS_CreateAccount");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username + "!!!!");
            }
        }

        subCodeButton = findViewById(R.id.submitQRCodeButton);
        generateQRCode = findViewById(R.id.generateQRCodeButton);
        mapView = (MapView) findViewById(R.id.appMapView);
        leaderBoard = findViewById(R.id.LeaderBoardButton);
        search = findViewById(R.id.Search);
        globalQRCodeList = findViewById(R.id.GlobalQRCodeListButton);
        deleteCode = findViewById(R.id.DeleteQRCodeButton);
        deletePlayer = findViewById(R.id.DeletePlayerButton);
        // map logic
        checkPermission();
        // dummy check for permission; need to add more details here
        if (isPermissionGranted){
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);
        }

        // qrcode logic
        generateQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseQRCodeType = new Intent(MainScreen.this, ChooseQRCodeType.class);
                chooseQRCodeType.putExtra(USER_NAME, username);
                chooseQRCodeType.putExtra(EMAIL_ADDRESS,email);
                startActivity(chooseQRCodeType);
            }
        });
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent searchUser = new Intent(MainScreen.this,SearchUser.class);
                startActivity(searchUser);
            }
        });
        leaderBoard.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseLeaderBoardType = new Intent(MainScreen.this, LeaderBoardType.class);
                chooseLeaderBoardType.putExtra("USER_NAME_MainScreen",username);
                startActivity(chooseLeaderBoardType);
            }
        });
        // listener for the submit qr code button
        subCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scanner scanner = new Scanner(view, MainScreen.this);
                scanner.startScan();
            }
        });
        globalQRCodeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent globalList = new Intent(MainScreen.this,GlobalQRCodeList.class);
                startActivity(globalList);
            }
        });
        deleteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbOwner = FirebaseFirestore.getInstance();
                CollectionReference collectionReferenceOwner = dbOwner.collection("Owner");
                collectionReferenceOwner.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                            for (int i = 0; i < documentList.size(); i++) {
                                int index = i;
                                if ((documentList.get(i).getId().toString().equals(username))) {
                                    Intent ownerGlobalList = new Intent(MainScreen.this, OwnerGlobalQRCodeList.class);
                                    startActivity(ownerGlobalList);
                                    return;
                                }
                                else{
                                    Toast.makeText(MainScreen.this,"You are not an owner",Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    }
                });
            }
        });
        deletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbOwner = FirebaseFirestore.getInstance();
                CollectionReference collectionReferenceOwner = dbOwner.collection("Owner");
                collectionReferenceOwner.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                            for (int i = 0; i < documentList.size(); i++) {
                                int index = i;
                                if ((documentList.get(i).getId().toString().equals(username))) {
                                    Intent ownerPlayerList = new Intent(MainScreen.this, OwnerGlobalPlayerList.class);
                                    startActivity(ownerPlayerList);
                                    return;
                                }
                                else{
                                    Toast.makeText(MainScreen.this,"You are not an owner",Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                    }
                });
            }
        });
        // sidebar logic
    }



    //need to change this when refactoring to improve cohesion

    /**
     * this is handling the qr code scanning and then saving. the actual saving is done within the QR code object.
     * @param requestCode request Code
     * @param resultCode result code
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null){

            QRCode qrCode = new QRCode(intentResult.getContents(), false);
            score = Integer.toString(qrCode.getScore());
            qrCodeHash = qrCode.getHash();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection("userScore");
            final CollectionReference collectionReferenceUserToQRCode = db.collection("UserToQRCode");
            final CollectionReference collectionReferenceQRCodetoUser = db.collection("QRCodeToUser");
            collectionReference.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    setUserScore(task, collectionReference);

                    collectionReferenceUserToQRCode.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            setUserQrRelation(task, collectionReferenceUserToQRCode);

                            collectionReferenceQRCodetoUser.document(qrCodeHash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    setQrUserRelation(task, collectionReferenceQRCodetoUser);
                                }
                            });
                        }
                    });
                }
            });
            openSubmissionActivity(qrCode);

        }else {
            Toast.makeText(getApplicationContext(), "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * makes the menu (the 3 dots in the upper right hand side) appear on the main screen page
     * @param menu menu
     * @return true
     */


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    /**method for dummy check
     *
     */


    private void checkPermission(){
        isPermissionGranted = true;
        return;
    }

    // MAP LIFE CYCLE METHODS
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // logic for how the map pans to device location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
    public void openSubmissionActivity(QRCode qrCode){
       Intent intent = new Intent(this, ScanSuccess.class);
       intent.putExtra("QRCODE", qrCode);
       startActivity(intent);

    }

    public void setUserScore(Task<DocumentSnapshot> task, CollectionReference collectionReference){
        if (task.getResult().exists()) {
            List<Integer> newScoreList = new ArrayList<Integer>();
            String list = task.getResult().get("Score:").toString();
            String[] string = list.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .replaceAll(" ", "")
                    .split(",");
            for (int i = 0; i < string.length; i++) {
                newScoreList.add(Integer.valueOf(string[i]));
            }
            newScoreList.add(parseInt(score));
            collectionReference.document(username).delete();
            HashMap<String, Object> userScore = new HashMap<>();
            userScore.put("Score:", newScoreList);
            collectionReference.document(username).set(userScore);
        } else {
            List<Integer> scoreList = new ArrayList<Integer>();
            scoreList.add(parseInt(score));
            HashMap<String, Object> userScore = new HashMap<>();
            userScore.put("Score:", scoreList);
            collectionReference.document(username).set(userScore);
        }
    }

    public void setUserQrRelation(Task<DocumentSnapshot> task, CollectionReference collectionReferenceUserToQRCode) {
        if (task.getResult().exists()) {
            List<String> newUserToQrcodeList = new ArrayList<String>();
            String userToQrcodelist = task.getResult().get("QRCode").toString();
            String[] userToQrcodestring = userToQrcodelist.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .replaceAll(" ", "")
                    .split(",");
            for (int i = 0; i < userToQrcodestring.length; i++) {
                newUserToQrcodeList.add(userToQrcodestring[i]);
            }
            newUserToQrcodeList.add(qrCodeHash);
            collectionReferenceUserToQRCode.document(username).delete();
            HashMap<String, Object> userQRCode = new HashMap<>();
            userQRCode.put("QRCode", newUserToQrcodeList);
            collectionReferenceUserToQRCode.document(username).set(userQRCode);
        } else {
            List<String> qrcodeList = new ArrayList<String>();
            qrcodeList.add(qrCodeHash);
            HashMap<String, Object> userQRCode = new HashMap<>();
            userQRCode.put("QRCode", qrcodeList);
            collectionReferenceUserToQRCode.document(username).set(userQRCode);
        }
    }

    public void setQrUserRelation(Task<DocumentSnapshot> task, CollectionReference collectionReferenceQRCodetoUser) {
        if (task.getResult().exists()) {
            List<String> newQrcodeToUserList = new ArrayList<String>();
            String qrcodeToUserlist = task.getResult().get("Username").toString();
            String[] qrcodeToUserstring = qrcodeToUserlist.replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .replaceAll(" ", "")
                    .split(",");
            for (int i = 0; i < qrcodeToUserstring.length; i++) {
                newQrcodeToUserList.add(qrcodeToUserstring[i]);
            }
            newQrcodeToUserList.add(username);
            collectionReferenceQRCodetoUser.document(qrCodeHash).delete();
            HashMap<String, Object> QRCodeUser = new HashMap<>();
            QRCodeUser.put("Username", newQrcodeToUserList);
            collectionReferenceQRCodetoUser.document(qrCodeHash).set(QRCodeUser);
        } else {
            List<String> usernameList = new ArrayList<String>();
            usernameList.add(username);
            HashMap<String, Object> QRCodeUser = new HashMap<>();
            QRCodeUser.put("Username", usernameList);
            collectionReferenceQRCodetoUser.document(qrCodeHash).set(QRCodeUser);
        }
    }
}