package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import org.json.JSONObject;

public abstract class CharacterClass extends EntityClass implements Character{
    protected int currentPlaceId;
    protected Inventory inventory;
    protected String title;
    protected int level;
    protected int health = 100;
    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }

    @Override
    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Place getCurrentPlace() {
        return world.getStorage().getPlace(currentPlaceId);
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
        object.put("title", getTitle());
        object.put("inventory", inventory.serialize());
        object.put("currentPlace", currentPlaceId);
        object.put("health", getHealth());
        object.put("level", getLevel());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
        setLevel(object.optInt("level", 1));
    }
}
