package com.example.qrquest;


import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    public static final String USER_NAME = "com.example.qrquest.USERNAME";
    public static final String EMAIL_ADDRESS = "com.example.qrquest.EMAILADDRESS";
    TextView welcomeMessage;
    private MapView map;
    IMapController mapController;
    ArrayList<OverlayItem> items;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Button generateQRCode;
    String username;
    String email;
    Button subCodeButton;
    Button deleteCode;
    Button deletePlayer;
    Button logout;
    String score;
    String qrCodeHash;
    FirebaseFirestore dbOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main_screen);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); // render map
        map.setBuiltInZoomControls(true); // zoomable
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController = map.getController();
                    mapController.setZoom(18.0);
                    mapController.setCenter(startPoint);
                    OverlayItem home = new OverlayItem("YOUR LOCATION", "Location",
                            new GeoPoint(location.getLatitude(), location.getLongitude()));
                    items = new ArrayList<>();
                    Drawable m = home.getMarker(0);
                    items.add(home);

                    ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(),
                            items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(int index, OverlayItem item) {
                            return true;
                        }

                        @Override
                        public boolean onItemLongPress(int index, OverlayItem item) {
                            return false;
                        }
                    });

                    mOverlay.setFocusItemsOnTap(true);
                    map.getOverlays().add(mOverlay);
                }
            }
        });
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (intent.containsKey("USER_NAME_MainActivity")){
                username = intent.getString("USER_NAME_MainActivity");
                email = intent.getString("EMAIL_ADDRESS_MainActivity");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username.substring(0, 8) + "!");
            }
            else{
                username = intent.getString("USER_NAME_CreateAccount");
                email = intent.getString("EMAIL_ADDRESS_CreateAccount");
                welcomeMessage = findViewById(R.id.welcomeUserEditText);
                welcomeMessage.setText("Welcome, " + username.substring(0, 8) + "!!!!");
            }
        }

        subCodeButton = findViewById(R.id.submitQRCodeButton);
        generateQRCode = findViewById(R.id.generateQRCodeButton);
        deleteCode = findViewById(R.id.DeleteQRCodeButton);
        deletePlayer = findViewById(R.id.DeletePlayerButton);
        logout = findViewById(R.id.LogoutButton);

        // qrcode logic
        generateQRCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent chooseQRCodeType = new Intent(MainScreen.this, ChooseQRCodeType.class);
                chooseQRCodeType.putExtra(USER_NAME, username);
                chooseQRCodeType.putExtra(EMAIL_ADDRESS,email);
                startActivity(chooseQRCodeType);
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
        deleteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOwnerActivity(OwnerGlobalQRCodeList.class);
            }
        });
        deletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOwnerActivity(OwnerGlobalPlayerList.class);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout = new Intent(MainScreen.this, ReLogin.class);
                startActivity(logout);
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
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection("userScore");
            final CollectionReference collectionReferenceUserToQRCode = db.collection("UserToQRCode");
            final CollectionReference collectionReferenceQRCodetoUser = db.collection("QRCodeToUser");

            QRCode qrCode = new QRCode(intentResult.getContents(), false);
            score = Integer.toString(qrCode.getScore());
            qrCodeHash = qrCode.getHash();


            collectionReference.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    collectionReferenceUserToQRCode.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                List<String> checkQRCodeList = new ArrayList<String>();
                                String list = task.getResult().get("QRCode").toString();
                                String[] string = list.replaceAll("\\[", "")
                                        .replaceAll("]", "")
                                        .replaceAll(" ", "")
                                        .split(",");
                                for (int i = 0; i < string.length; i++) {
                                    checkQRCodeList.add(string[i]);
                                }
                                if (checkQRCodeList.contains(qrCodeHash)) {
                                    Toast.makeText(getApplicationContext(), "You have already scanned this QR Code", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    Toast.makeText(getApplicationContext(), "You have never scanned this QR Code", Toast.LENGTH_SHORT).show();
                                    //setUserScore(task, collectionReference);
                                    if (task.getResult().exists()) {
                                        collectionReference.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                setUserScore(task,collectionReference);
                                                collectionReferenceUserToQRCode.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        setUserQrRelation(task, collectionReferenceUserToQRCode);

                                                        collectionReferenceQRCodetoUser.document(qrCodeHash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                setQrUserRelation(task, collectionReferenceQRCodetoUser);
                                                                openSubmissionActivity(qrCode);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
            });

        }else {
            Toast.makeText(getApplicationContext(), "Nothing was scanned.", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.leaderboardMenuItem:{
                Intent chooseLeaderBoardType = new Intent(MainScreen.this, LeaderBoardType.class);
                chooseLeaderBoardType.putExtra("USER_NAME_MainScreen",username);
                startActivity(chooseLeaderBoardType);
                return true;
            }
            case R.id.userSearchItem:{
                Intent searchUser = new Intent(MainScreen.this,SearchUser.class);
                startActivity(searchUser);
                return true;
            }
            case R.id.viewQrCodesItem:{
                Intent globalList = new Intent(MainScreen.this,GlobalQRCodeList.class);
                startActivity(globalList);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // MAP LIFE CYCLE METHODS
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    private void openSubmissionActivity(QRCode qrCode){
        Intent intent = new Intent(this, ScanSuccess.class);
        intent.putExtra("QRCODE", qrCode);
        startActivity(intent);

    }

    private void setUserScore(@NonNull Task<DocumentSnapshot> task, CollectionReference collectionReference){
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

    private void setUserQrRelation(@NonNull Task<DocumentSnapshot> task, CollectionReference collectionReferenceUserToQRCode) {
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

    private void setQrUserRelation(@NonNull Task<DocumentSnapshot> task, CollectionReference collectionReferenceQRCodetoUser) {
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

    private void runOwnerActivity(Class activity) {
        dbOwner = FirebaseFirestore.getInstance();
        final boolean[] isOwner = new boolean[1];
        CollectionReference collectionReferenceOwner = dbOwner.collection("Owner");
        collectionReferenceOwner.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isOwner = false;
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                    for (int i = 0; i < documentList.size(); i++) {
                        int index = i;
                        if ((documentList.get(i).getId().toString().equals(username))) {
                            isOwner = true;
                            Intent ownerPlayerList = new Intent(MainScreen.this, activity);
                            startActivity(ownerPlayerList);
                        }
                    }
                    if(!isOwner) {
                        Toast.makeText(MainScreen.this,"You are not an owner",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void checkIfFound(FirebaseFirestore db){
        final CollectionReference collectionReference = db.collection("QRCodes");
        if (collectionReference.document("testDoc") == null) {
            Toast.makeText(MainScreen.this,"the doc does not exist",Toast.LENGTH_SHORT).show();
        }
    }

}