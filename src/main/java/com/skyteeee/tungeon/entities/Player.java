package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;

public class Player extends EntityClass implements Character {

    private int currentPlaceId;

    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) Storage.getInstance().getEntity(currentPlaceId);
    }


}
