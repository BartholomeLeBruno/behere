package com.esgi.behere.actor;


import java.util.Date;

public class Publication implements Comparable<Publication> {

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

    private long from_id;

    public long getFrom_id() {
        return from_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public void setFrom_id(long from_id) {
        this.from_id = from_id;
    }

    public Publication(String pseudo, String content, Date created_at, long from_id, String type)
    {
        this.pseudo = pseudo;
        this.content = content;
        this.created_at = created_at;
        this.from_id = from_id;
        this.type = type;
    }


    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    private Date created_at;

    @Override
    public int compareTo(Publication o) {
        return getCreated_at().compareTo(o.getCreated_at());
    }
}
