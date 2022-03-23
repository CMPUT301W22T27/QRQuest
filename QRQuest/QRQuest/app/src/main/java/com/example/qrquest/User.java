package com.example.qrquest;

/**
 * Represents the user
 */
public class User {

    private String userID; // unique device id
    private String userDisplay; // what user displays on app; can be changed later; defaults to userID

    /**
     * Constructor method for user, assigns the username and email provided by the user
     * @param userID
     * @param userDisplay
     */

    User(String userID, String userDisplay){
        this.userID = userID;
        this.userDisplay = userDisplay;
    }

    /**
     * returns username
     * @return username
     */

    String getUserID() {return this.userID; }

    /**
     * returns email
     * @return email
     */
    String getUserDisplay() {return this.userDisplay; }

    String setUserDisplay(String newUserDisplay) {
        this.userDisplay = newUserDisplay;
        return newUserDisplay;
    }
}
