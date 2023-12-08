package com.skyteeee.tungeon.storage;

import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.utils.Savable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Inventory implements Savable {
    private final List<Integer> inventory = new ArrayList<>();

    private final int maxSize;

    public Inventory() {
        this(3);
    }

    public Inventory(int maxSize) {
        this.maxSize = maxSize;
    }

    public Item addItem(Item item) {
        Item popped = null;
        if (inventory.size() >= maxSize) {
            popped = getItem(0);
            inventory.remove(0);
        }
        inventory.add(item.getId());
        return popped;
    }

    public void addItem(int id) {
        inventory.add(id);
    }

    public void removeItem(int id) {
        inventory.remove((Integer) id);
    }

    public void removeItem(Item item) {
        inventory.remove((Integer) item.getId());
    }

    public Item getItem(int index) {
        Storage storage = Storage.getInstance();
        return storage.getItem(inventory.get(index));
    }

    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    public int size() {
        return inventory.size();
    }

    public void printState(boolean detailed) {
        Storage storage = Storage.getInstance();
        for(int i = 0; i < inventory.size(); i++) {
            Item item = storage.getItem(inventory.get(i));
            System.out.print((i+1) + ": " + item.getTitle());
            if (detailed) {
                if (item instanceof Weapon weapon) {
                    System.out.println(" | Damage: " + weapon.getDamage());
                }
            } else System.out.println();
        }
    }

    public void dropAll(Place place) {
        Storage storage = Storage.getInstance();
        for(int i = 0; i < inventory.size(); i++) {
            Item item = storage.getItem(inventory.get(i));
            item.drop(place);
        }
        inventory.clear();
    }


    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("inventory", inventory);
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        JSONArray inventoryArray = object.getJSONArray("inventory");
        for (int i = 0; i < inventoryArray.length(); i++) {
            addItem(inventoryArray.getInt(i));
        }
    }
}
