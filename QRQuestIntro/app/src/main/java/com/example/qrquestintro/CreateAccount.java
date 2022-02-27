package com.example.qrquestintro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {
    EditText userName;
    EditText Email;
    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        confirm = findViewById(R.id.confirm);
        HashMap<String, String> data = new HashMap<>();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = findViewById(R.id.username);
                Email = findViewById(R.id.email);
                if (userName.length()>0 && Email.length()>0) {
                    data.put("UserName", userName.toString());
                }
                // Go to Welcome Class
            }
        });
    }
}