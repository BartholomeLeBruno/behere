package com.esgi.behere.actor;

import java.util.Date;

class Comment implements  Comparable<Comment>{

    private String text;

    private long id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    private Date created_at;


    @Override
    public int compareTo(Comment o) {
            return getCreated_at().compareTo(o.getCreated_at());
    }
}
