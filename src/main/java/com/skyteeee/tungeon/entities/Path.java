package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;

public class Path extends EntityClass {

    private int[] places = new int[2];
    private String title;

    public void setPlaces(Place p1, Place p2) {
        places[0] = p1.getId();
        places[1] = p2.getId();
        p1.addPath(this);
        p2.addPath(this);
    }

    public Place getDestination(Place location) {
        int locationId = location.getId();
        int destinationId = locationId == places[0] ? places[1] : places[0];
        return Storage.getInstance().getPlace(destinationId);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
