package com.example.qrquest;

import com.google.common.hash.Hashing;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QRCode {
    //    private  String value;
    private String hash;
    private Integer score;
    // we should include the location here

    public QRCode(String value) {
        computeHash(value);
        computeScore();
    }

    public String getHash() {
        return hash;
    }

    private void computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        this.hash = sha256hex;
    }

    public int getScore() {
        return this.score;
    }

    private void computeScore() {
        int currentScore = 0;
        String currentHex = "";
        ArrayList<String> repetitions = getRepetitions();
        for (String rep : repetitions) {
            currentHex = "";
            currentHex += rep.charAt(0);
            if (Integer.decode("0x" + currentHex) == 0) {
                currentScore += Math.pow(20, rep.length()-1);
            } else {
                currentScore += Math.pow(Integer.decode("0x" + currentHex), rep.length()-1);
            }
        }
        this.score = currentScore;

    }

    private ArrayList getRepetitions(){

//        This is just a test case that Mikal was using, make sure to remove later
//        String test = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
//        char[] chars= test.toCharArray();


        ArrayList<String> allReps = new ArrayList<>();
        char[] chars= this.hash.toCharArray();
        char currentChar;
        char prevChar = chars[0];
        String reps = "";
        reps = reps + prevChar;

        for (int i = 1; i < chars.length; i++) {
            currentChar = chars[i];
            if (prevChar == currentChar){
                reps += currentChar;
            } else {
                if (reps.length() >=2) {
                    allReps.add(reps);
                }
                prevChar = currentChar;
                reps = "" + prevChar;
            }
        }
        return allReps;
    }
}


