package com.esgi.behere.actor;

import java.util.Date;

public class Reservation {

    private int numberOfPeople;

    private Date arrivalTime;

    private boolean status;

    private long id;

    private long user_id;

    private long bar_id;

    public Reservation() { }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getBar_id() {
        return bar_id;
    }

    public void setBar_id(long bar_id) {
        this.bar_id = bar_id;
    }
}
