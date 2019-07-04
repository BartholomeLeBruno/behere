package com.esgi.behere.actor;

public class Notification {

    private long id;

    public Notification(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getOther_user_id() {
        return other_user_id;
    }

    public void setOther_user_id(long other_user_id) {
        this.other_user_id = other_user_id;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    private String text;

    private String type;

    public Notification(String text, String type, long user_id, long other_user_id, long group_id) {
        this.text = text;
        this.type = type;
        this.user_id = user_id;
        this.other_user_id = other_user_id;
        this.group_id = group_id;
    }

    private long user_id;

    private long other_user_id;

    private long group_id;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    private String groupname;


}
