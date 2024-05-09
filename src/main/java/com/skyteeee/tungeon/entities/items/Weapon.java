package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

public class Weapon extends EntityClass implements Breakable{
    private String title;
    private int damage;
    private float dropChance;
    private int level;
    private float durability = 1;
    private float resistance = 1;

    public static final Weapon BARE_HANDS = new Weapon();
    static {
        BARE_HANDS.setTitle("bear hands");
        BARE_HANDS.setDamage(2);
        BARE_HANDS.setDurability(100);
        BARE_HANDS.setResistance(1f);
        BARE_HANDS.setDropChance(0f);
        BARE_HANDS.setLevel(1);
    }

    @Override
    public String getTitle() {
        return getTitle(false);
    }
    public String getTitle(boolean raw) {
        return raw ? title : title + " " + level + " | Durability: " + UserInterface.floor(durability, 1);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setDamage(int dmg) {
        damage = dmg;
    }

    public int getDamage() {
        return damage;
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
        float ourDamage = damage * 0.5f;
        float durabilityLost = (1-getResistance()) * ourDamage;
        setDurability(getDurability()-durabilityLost);
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
        object.put("level", getLevel());
        object.put("title", getTitle(true));
        object.put("damage", getDamage());
        object.put("durability", getDurability());
        object.put("resistance", getResistance());
        object.put("dropChance", getDropChance());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setLevel(object.optInt("level", 1));
        setDamage(object.getInt("damage"));
        setDropChance(object.getFloat("dropChance"));
        setDurability(object.optFloat("durability", 1f));
        setResistance(object.optFloat("resistance", 1f));
    }
}
