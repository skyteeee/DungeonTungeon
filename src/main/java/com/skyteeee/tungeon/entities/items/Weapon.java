package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.utils.EntityFactory;
import org.json.JSONObject;

public class Weapon extends EntityClass implements Item{
    private String title;
    private int damage;
    private float dropChance;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDamage(int dmg) {
        damage = dmg;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void setDropChance(float chance) {
        dropChance = chance;
    }

    @Override
    public float getDropChance() {
        return dropChance;
    }

    @Override
    public void drop(Place place) {
        if (EntityFactory.rnd.nextFloat() < getDropChance()) {
            place.take(this);
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle());
        object.put("damage", getDamage());
        object.put("dropChance", getDropChance());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setDamage(object.getInt("damage"));
        setDropChance(object.getFloat("dropChance"));
    }
}
