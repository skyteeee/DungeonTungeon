package com.skyteeee.tungeon.storage;

import com.skyteeee.tungeon.entities.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Integer> inventory = new ArrayList<>();

    public void addItem(Item item) {
        inventory.add(item.getId());
    }

    public void addItem(int id) {
        inventory.add(id);
    }

    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    public void printState() {
        Storage storage = Storage.getInstance();
        for(int i = 0; i < inventory.size(); i++) {
            System.out.println((i+1) + ": " + storage.getItem(inventory.get(i)).getTitle());
        }
    }

}
