package com.example.zavrsnirad.model;

import android.util.Patterns;

import java.util.ArrayList;

public class User {
    private String fullName,email,password;
    private ArrayList<String> predmeti;

    public User() { }

    public User(String fullName, String email, String password, ArrayList<String> predmeti) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.predmeti = predmeti;
    }

    public User(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public ArrayList<String> getPredmeti() {
        return predmeti;
    }

    public void setPredmeti(ArrayList<String> predmeti) {
        this.predmeti = predmeti;
    }

    public String validatePassword(String passwordText) {
        if (passwordText.isEmpty()) {
            return "Password je obavezan!";
        } else if (passwordText.length() < 8) {
            return "Password mora imati najmanje 8 karaktera!";
        }
        return "ok";
    }

    public String validateEmail(String emailText) {
        if (emailText.isEmpty()) {
            return "Email je obavezan!";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Unesite validan email!";
        }
        return "ok";
    }

    public String validateFirstName(String firstNameText) {
        if (firstNameText.isEmpty()) {
            return "Ime je obavezno!";
        }
        return "ok";
    }

    public String validateLastName(String lastNameText) {
        if (lastNameText.isEmpty()) {
            return "Prezime je obavezno!";
        }
        return "ok";
    }
}
