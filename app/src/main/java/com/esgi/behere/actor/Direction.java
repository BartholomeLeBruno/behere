package com.esgi.behere.actor;

import com.google.android.gms.maps.model.LatLng;

public class Direction {

    private LatLng depart;

    private LatLng arrivee;

    public LatLng getDepart() {
        return depart;
    }

    private void setDepart(LatLng depart) {
        this.depart = depart;
    }

    private LatLng getArrivee() {
        return arrivee;
    }

    private void setArrivee(LatLng arrivee) {
        this.arrivee = arrivee;
    }

    public  Direction(LatLng arrivee)
    {
        this.arrivee = arrivee;
    }



}
