package com.example.qrquest;

public class Data {
    public String rank;
    public String username;
    public String score;

    public Data(String rank, String username, String score) {
        this.rank = rank;
        this.username = username;
        this.score = score;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
