package com.example.qrquest;

/**
 * Represents the user
 */
public class User {

    private String username;
    private String email;

    /**
     * Constructor method for user, assigns the username and email provided by the user
     * @param username
     * @param email
     */

    User(String username, String email){
        this.username = username;
        this.email = email;
    }

    /**
     * returns username
     * @return username
     */

    String getUsername() {return this.username; }

    /**
     * returns email
     * @return email
     */
    String getEmail() {return this.email; }
}
