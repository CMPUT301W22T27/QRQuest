package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class QRCodeProfile extends AppCompatActivity {
    Button otherUser;
    String QRCode;
    TextView qrCodeNameBox;
    FirebaseFirestore db;
    private MapView map;
    IMapController mapController;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_profile);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); // render map
        map.setBuiltInZoomControls(true); // zoomable
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
        qrCodeNameBox = findViewById(R.id.QRCodeProfileName);
        //qrCodeNameBox.setText("Name of the QR Code:"+'\n'+QRCode);
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
        collectionReference.document(QRCode).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String qrCodeName =  task.getResult().get("name").toString();
                    double qrCodeLat = Double.parseDouble(task.getResult().get("Latitude").toString());
                    double qrCodeLong = Double.parseDouble(task.getResult().get("Longitude").toString());
                    qrCodeNameBox.setText("Name of the QR Code:"+'\n'+qrCodeName);
                    GeoPoint startPoint = new GeoPoint(qrCodeLat, qrCodeLong);
                    mapController = map.getController();
                    mapController.setZoom(18.0);
                    mapController.setCenter(startPoint);
                }
            }
            });
    }
}