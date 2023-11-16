package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONObject;

public class Enemy extends EntityClass implements Character{
    private int currentPlaceId;
    private Inventory inventory = new Inventory();
    private String title;
    private int weaponIdx = 0;

    private int health = 100;

    @Override
    public void setCurrentPlace(int id) {

        currentPlaceId = id;
    }

    @Override
    public void setCurrentPlace(Place place) {
        place.addEnemy(this);
        currentPlaceId = place.getId();
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
    public void take(int choice) {

    }

    @Override
    public Item give(int choice) {
        return null;
    }

    public void printState() {
        System.out.println(title + " holding a " + inventory.getItem(weaponIdx).getTitle());
    }

    @Override
    public void defend(Character attacker, Weapon weapon) {
        health -= weapon.getDamage();
        if (!checkDeath()) {
            attack(attacker, (Weapon) inventory.getItem(weaponIdx));
        }
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        if (weapon == null) {
            weapon = (Weapon) inventory.getItem(weaponIdx);
        }
        target.defend(this, weapon);
    }

    private boolean checkDeath() {
        if (health <= 0) {
            System.out.println("KILLED: " + title);
            inventory.dropAll(getCurrentPlace());
            getCurrentPlace().removeEnemy(this);
            return true;
        }
        return false;
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
        object.put("title", getTitle());
        object.put("inventory", inventory.serialize());
        object.put("currentPlace", currentPlaceId);
        object.put("health", getHealth());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
