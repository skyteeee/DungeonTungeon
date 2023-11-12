package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Inventory;

public interface Character extends Entity {

    void setCurrentPlace(Place place);
    void setCurrentPlace(int id);

    Place getCurrentPlace();

    Inventory getInventory();

    void take(int choice);

    Item give(int choice);

    int getHealth();
    void setHealth(int health);

}
