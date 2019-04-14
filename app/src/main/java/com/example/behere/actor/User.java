package com.example.behere.actor;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String email;

    private String password;

    private String name;

    private String surname;

    private String birthDate;

    private String pathPicture;

    private List<User> listAmis = new ArrayList<>();

    public User()
    {

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPathPicture() {
        return pathPicture;
    }

    public void setPathPicture(String pathPicture) {
        this.pathPicture = pathPicture;
    }

    public List<User> getListAmis() {
        return listAmis;
    }

    public void setListAmis(List<User> listAmis) {
        this.listAmis = listAmis;
    }


}
