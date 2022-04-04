package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class QRCodeProfile extends AppCompatActivity {
    Button otherUser;
    String QRCode;
    TextView qrCodeNameBox;
    FirebaseFirestore db;
    ImageView qrItemImage;
    ArrayList<OverlayItem> items;
    private MapView map;
    IMapController mapController;
    Button seeComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_profile);
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            if (intent.containsKey("QRCode_TotalNumberQRCodeScanned")){
                QRCode= intent.getString("QRCode_TotalNumberQRCodeScanned");
            }
            else if(intent.containsKey("QRCode_OtherPlayerQRCodeCount")){
                QRCode = intent.getString("QRCode_OtherPlayerQRCodeCount");
            }
            else{
                QRCode = intent.getString("QRCode_GlobalQRCodeList");
            }
        }

        map = findViewById(R.id.map);
        qrCodeNameBox = findViewById(R.id.QRCodeProfileName);
        qrItemImage = findViewById(R.id.ItemImageView);
        //qrCodeNameBox.setText("Name of the QR Code:"+'\n'+QRCode);
        seeComments = findViewById(R.id.seeComments);
        seeComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent seeComments = new Intent(QRCodeProfile.this, Seecomments.class);
                seeComments.putExtra("QRCode_QRCodeProfile",QRCode);
                startActivity(seeComments);
            }
        });
        otherUser = findViewById(R.id.OtherUserButton);
        otherUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent otherUserWithSameQRCode = new Intent(QRCodeProfile.this, OtherUserWithSameQRCode.class);
                otherUserWithSameQRCode.putExtra("QRCode_QRCodeProfile",QRCode);
                startActivity(otherUserWithSameQRCode);
            }
        });
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("QRCodes");
        CollectionReference collectionLocReference = db.collection("QRCodeLocs");
        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String qrCodeName =  task.getResult().get("name").toString();
                    String encodedImage = task.getResult().get("Image").toString();
                    // converting the image back to a bitmap from a string

                    if (encodedImage != "") {
                        byte[] decodedArray = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap image = BitmapFactory.decodeByteArray(decodedArray, 0, decodedArray.length);
                        qrItemImage.setImageBitmap(image);
                    }

                    qrCodeNameBox.setText("Name of the QR Code:"+'\n'+qrCodeName);

                }
            }
        });

        collectionLocReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    double qrCodeLat = Double.parseDouble(task.getResult().get("Latitude").toString());
                    double qrCodeLong = Double.parseDouble(task.getResult().get("Longitude").toString());
                    String qrCodeScore = task.getResult().get("Score").toString();
                    GeoPoint qrCodeLocation = new GeoPoint(qrCodeLat, qrCodeLong);
                    mapController = map.getController();
                    mapController.setZoom(18.0);
                    mapController.setCenter(qrCodeLocation);
                    String displayName = qrCodeNameBox.getText().toString();
                    OverlayItem home = new OverlayItem(displayName.substring(21, displayName.length()),
                            qrCodeScore,
                            qrCodeLocation);
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
    }


}