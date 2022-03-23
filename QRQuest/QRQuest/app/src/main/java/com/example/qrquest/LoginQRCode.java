package com.example.qrquest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * Computes login QRCode
 */
public class LoginQRCode extends AppCompatActivity {
    public ImageView qrCodeImage;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_qrcode);
        Intent intent = getIntent();
        String userID = intent.getStringExtra(ChooseQRCodeType.USER_ID);
        String userDisplay = intent.getStringExtra(ChooseQRCodeType.USER_DISPLAY);
        TextView textUser = findViewById(R.id.textUserDisplay);
        textUser.setText(userDisplay.substring(0, 8));

        //Create QR Code
        qrCodeImage = findViewById(R.id.QrcodeImage);
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;
        String hash = computeHash(userID);
        qrgEncoder = new QRGEncoder(hash, null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("LoginQRCodes:");
        HashMap<String, String> data = new HashMap<>();
        // add tests for invalid usernames and emails later.
        data.put("UserID", userID);
        collectionReference.document(hash).set(data);
    }

    /**
     * Computes the hash of the score
     * @param value
     * @return hash of the score
     */
    private String computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
  }


  public void loginPressed(View view){
      IntentIntegrator intentIntegrator = new IntentIntegrator(
              LoginQRCode.this
      );
      intentIntegrator.setPrompt("For flash use volume up key");
      intentIntegrator.setBeepEnabled(false);
      intentIntegrator.setOrientationLocked(true);
      intentIntegrator.setCaptureActivity(Capture.class);
      intentIntegrator.initiateScan();
  }

  @Override
  protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginQRCode.this);

            QRCode qrCode = new QRCode(intentResult.getContents(), true);
            db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection("LoginQRCodes:");
            Log.i("Unhashed:", intentResult.getContents());
//            Log.i("Hashed:",  qrCode.getHash());
            collectionReference.document(qrCode.getHash()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> taskout) {
                    if (taskout.getResult().exists()) {
                        Intent intent = new Intent(LoginQRCode.this, MainScreen.class);
                        final CollectionReference collectionReferencein = db.collection("Users");
                        String userID = taskout.getResult().getString("UserID");
                        collectionReferencein.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> taskin) {
                                String userDisplay = taskin.getResult().getString("Username");
                                intent.putExtra("USER_ID_MainActivity", userID);
                                intent.putExtra("USER_DISPLAY_MainActivity", userDisplay);
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