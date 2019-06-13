package com.esgi.behere.actor;


import java.util.ArrayList;

public class Publication {

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    private String pseudo;


    public Publication(String pseudo, String content)
    {
        this.pseudo = pseudo;
        this.content = content;
    }

    public static ArrayList<Publication> createPublicationList(int number)
    {
        ArrayList<Publication> publications = new ArrayList<>();
        for (int i = 0; i <number ; i++) {
            publications.add(new Publication("voila","truc"));
        }
        return publications;
    }
}
