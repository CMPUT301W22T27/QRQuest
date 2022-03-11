package com.example.qrquest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainScreen extends AppCompatActivity {

    TextView welcomeMessage;
    Button viewLocButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Intent intent = getIntent();
        String username = intent.getStringExtra(CreateAccount.USER_NAME);
        viewLocButton = findViewById(R.id.viewMapButton);
        welcomeMessage = findViewById(R.id.welcomeUserEditText);
        welcomeMessage.setText("Welcome, " + username + "!");

        viewLocButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainScreen.this, MainScreenActivity.class);
                startActivity(intent);
            }
        });
    }


    public void scanner(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(
                MainScreen.this
        );
        intentIntegrator.setPrompt("For flash use volume up key");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            builder.setTitle("Result");

            QRCode qrCode = new QRCode(intentResult.getContents());
            String score = Integer.toString(qrCode.getScore());
            builder.setMessage(score);


//            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            builder.show();

        }else {
            Toast.makeText(getApplicationContext(), "OOPS... You did not scan anything", Toast.LENGTH_SHORT).show();
        }


    }
}