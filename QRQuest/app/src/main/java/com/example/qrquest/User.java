package com.example.qrquest;

public class User {

    private String username;
    private String email;

    User(String username, String email){
        this.username = username;
        this.email = email;
    }

    String getUsername() {return this.username; }
    String getEmail() {return this.email; }
}
