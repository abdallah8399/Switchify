package com.wingy.myapplication;

public class User {
    private String email, password, name, imageURL, ID;
    private int balance;

    public User(String email, String password, String name, int balance, String imageURL, String ID) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.balance = balance;
        this.imageURL = imageURL;
        this.ID = ID;

    }

    public User() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
