package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private final Inventory inventory = new Inventory();
    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }

    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    @Override
    public void take(int choice) {
        Item item = getCurrentPlace().give(choice);
        inventory.addItem(item);
        System.out.println("You took a " + item.getTitle());
    }

    public void printInventory() {
        System.out.println("------- \nYou have the following items: ");
        inventory.printState();
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) Storage.getInstance().getEntity(currentPlaceId);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("currentPlace", currentPlaceId);
        object.put("inventory", inventory.serialize());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
    }
}
