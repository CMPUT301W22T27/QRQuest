package com.example.qrquest;
//Represents the QR code, computes its scoring and hashing, saves the score to the database
//At this time, the cohesion of this class is high and need to be work upon for the final project.

import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the QR code, computes its scoring and hashing, saves the score to the database
 */
public class QRCode{
    //    private  String value;
    private String hash;
    private Integer score;
    // we should include the location here

    /**
     * Constructor method for the QRCode
     * Calls the compute hash function if the input value is not already hashed
     * Computes the score for the value
     * @param value that need to be hashed
     * @param isHashed Boolean Value to check if the string is already hashed
     */

    public QRCode(String value, Boolean isHashed) {
        if (!isHashed){
            computeHash(value);
        }
        else {
            this.hash = value;
        }

        computeScore();
    }

    /**
     * returns the hash value
     * @return hash value
     */

    public String getHash() {
        return this.hash;
    }

    /**
     * computes the hash for the string
     * @param value that needs to be hashed
     */

    public void computeHash(String value){
        String sha256hex = Hashing.sha256()
                .hashString(value, StandardCharsets.UTF_8)
                .toString();
        this.hash = sha256hex;
    }

    /**
     * returns the score of the QRCode
     * @return score
     */

    public int getScore() {
        return this.score;
    }

    /**
     * Computes the score of the QRCode
     */

    public void computeScore() {
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

    /**
     * saves the score of the Qrcode in the firebase
     */
    public void saveScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("QRCodes");

        HashMap<String, Integer> data = new HashMap<>();
        // add tests for invalid usernames and emails later.
        data.put("Score", this.score);


        collectionReference.document(this.hash).set(data);

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


