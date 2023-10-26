package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONObject;

public class Player extends EntityClass implements Character {

    private int currentPlaceId;

    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }

    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) Storage.getInstance().getEntity(currentPlaceId);
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("currentPlace", currentPlaceId);
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setCurrentPlace(object.getInt("currentPlace"));
    }
}
