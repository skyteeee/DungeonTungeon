package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;

public interface Character extends Entity {

    void setCurrentPlace(Place place);
    void setCurrentPlace(int id);

    void setLevel(int level);
    int getLevel();

    void setTitle(String title);

    String getTitle();

    Place getCurrentPlace();

    Inventory getInventory();

    void take(int choice);

    Item give(int choice);

    int getHealth();
    void setHealth(int health);

    void attack(Character target, Weapon weapon);
    void defend(Character attacker, Weapon weapon);

}
