package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
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
    public JSONObject serialize() {
        return null;
    }

    @Override
    public void deserialize(JSONObject object) {

    }
}
