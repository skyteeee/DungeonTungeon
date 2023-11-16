package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private final Inventory inventory = new Inventory();
    private int health = 1000;
    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }
    @Override
    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    @Override
    public void take(int choice) {
        Item item = getCurrentPlace().give(choice);
        inventory.addItem(item);
        System.out.println("You took a " + item.getTitle());
    }

    @Override
    public Item give(int choice) {
        Item item = inventory.getItem(choice);
        inventory.removeItem(item);
        System.out.println("You dropped a " + item.getTitle());
        return item;
    }

    public void printInventory() {
        UserInterface.strike();
        System.out.println("You have the following items: ");
        inventory.printState(true);
    }

    public void attack(int enemyIdx, UserInterface ui) {
        Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
        if (inventory.isEmpty()) {
            System.out.println("As you leap towards the enemy, you realize that you lack any weapons. It is too late to turn away now. ");
            enemy.attack(this, null);
        } else {
            System.out.println("You have the following items: ");
            inventory.printState(true);
            int weaponIdx = ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1;
            Weapon weapon = (Weapon) inventory.getItem(weaponIdx);
            attack(enemy, weapon);
        }
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        target.defend(this, weapon);
    }

    @Override
    public void defend(Character attacker, Weapon weapon) {
        health -= weapon.getDamage();
        checkDeath();
    }

    private void checkDeath() {
        if (health <= 0) {
            UserInterface.slowPrint("Wait", 100);
            for (int i = 0; i < 3; i++) {
                UserInterface.sleep(400);
                System.out.print(".");
            }
            System.out.println();
            UserInterface.sleep(1000);
            System.out.println("Why is it so cold and dark here?");
            UserInterface.sleep(1000);
            System.out.println("YOU DIED. HOW PITIFUL...");

            inventory.dropAll(getCurrentPlace());
        }
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
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("currentPlace", currentPlaceId);
        object.put("inventory", inventory.serialize());
        object.put("health", getHealth());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
    }
}
