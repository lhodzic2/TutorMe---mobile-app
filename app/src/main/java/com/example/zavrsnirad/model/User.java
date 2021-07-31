package com.example.zavrsnirad.model;

import android.util.Patterns;

public class User {
    private String firstName,lastName,email,password;

    public User() { }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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
