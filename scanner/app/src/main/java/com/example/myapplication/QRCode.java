package com.example.myapplication;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class QRCode {
    private  value;
    private String hash;
    private int score;

    public QRCode(String value) {
        this.value = value;
    }

    private void computeHash(){
        String sha256hex = Hashing.sha256()
                .hashString(this.value, StandardCharsets.UTF_8)
                .toString();
        this.hash = sha256hex;
    }

    public String getHash() {
        computeHash();
        return hash;
    }

    private void computeScore() {
        this.score = 5;

    }
}


