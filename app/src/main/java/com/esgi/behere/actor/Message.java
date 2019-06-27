package com.esgi.behere.actor;

public class Message {

    private long idMessage;

    public long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(long idMessage) {
        this.idMessage = idMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getUser_sender_id() {
        return user_sender_id;
    }

    public void setUser_sender_id(long user_sender_id) {
        this.user_sender_id = user_sender_id;
    }

    public long getUser_receiver_id() {
        return user_receiver_id;
    }

    public void setUser_receiver_id(long user_receiver_id) {
        this.user_receiver_id = user_receiver_id;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    private String textMessage;

    private long user_sender_id;

    private long user_receiver_id;

    private long group_id;

    public Message(){}

}
