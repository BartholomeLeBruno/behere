package com.example.behere.actor;

import java.util.ArrayList;

public class Publication {

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    private Publication(String content)
    {
        this.content = content;
    }

    public static ArrayList<Publication> createPublicationList(int numPublication) {
        ArrayList<Publication> publications = new ArrayList<>();

        for (int i = 1; i <= numPublication; i++) {
            publications.add(new Publication("Publication numero : "+i));
        }

        return publications;
    }
}
