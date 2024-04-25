package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

public class Armor extends EntityClass implements Breakable {

    private String title;
    private float dropChance;
    private int defence = 10;
    private float absorption = 0.9f;

    private float durability = 1f;
    private float resistance = 1f;
    int level = 1;
    @Override
    public String getTitle() {
        return getTitle(false);
    }
    public String getTitle(boolean raw) {
        return title + (raw ? "" : " " + level + " | Durability: " + UserInterface.floor(durability,1));
    }

    public String getShortTitle() {
        String[] parts = title.split(" ");
        return parts[0] + " " + parts[parts.length-1];
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
    public void setDurability(float durability) {
        this.durability = durability;
    }

    @Override
    public float getDurability() {
        return durability;
    }

    @Override
    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    @Override
    public float getResistance() {
        return resistance;
    }

    @Override
    public void applyDamage(int damage) {
        float ourDamage = damage * (1-getAbsorption());
        float durabilityLost = (1-getResistance()) * ourDamage;
        setDurability(getDurability()-durabilityLost);
    }

    @Override
    public void drop(Place place) {
        if (EntityFactory.rnd.nextFloat() < getDropChance()) {
            place.take(this);
        }
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    //during attack, damage is multiplied by raw absorption. simple absorption (inverted) is how much is ACTUALLY being ABSORBED. sorry.
    public float getAbsorption() {
        return getAbsorption(false);
    }
    public float getAbsorption(boolean simple) {
        return simple ? ((int)((1 - absorption) * 100) / 100f) : absorption;
    }

    public void setAbsorption(float absorption) {
        this.absorption = absorption;
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle(true));
        object.put("defence", getDefence());
        object.put("durability", getDurability());
        object.put("resistance", getResistance());
        object.put("absorption", getAbsorption());
        object.put("dropChance", getDropChance());
        object.put("level", getLevel());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setDefence(object.getInt("defence"));
        setAbsorption(object.getFloat("absorption"));
        setDropChance(object.getFloat("dropChance"));
        setLevel(object.optInt("level", 1));
        setDurability(object.optFloat("durability", 1f));
        setResistance(object.optFloat("resistance", 1f));
    }
}
