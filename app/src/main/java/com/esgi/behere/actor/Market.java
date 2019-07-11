package com.esgi.behere.actor;

import java.io.Serializable;

public abstract class Market implements Serializable {

    private long id;

    private String name;

    private Double latitude;

    private Double longitutde;

    private String description;

    private String webSiteLink;

    private String type;

    private String facebookLink;


    Market(long id, String name, Double latitude, Double longitutde, String description, String webSiteLink, String facebookLink, String type) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitutde = longitutde;
        this.description = description;
        this.webSiteLink = webSiteLink;
        this.type = type;
        this.facebookLink = facebookLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitutde() {
        return longitutde;
    }

    public void setLongitutde(Double longitutde) {
        this.longitutde = longitutde;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebSiteLink() {
        return webSiteLink;
    }

    public void setWebSiteLink(String webSiteLink) {
        this.webSiteLink = webSiteLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }
}
