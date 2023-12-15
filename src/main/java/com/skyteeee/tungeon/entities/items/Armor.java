package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.utils.EntityFactory;
import org.json.JSONObject;

public class Armor extends EntityClass implements Item {

    private String title;
    private float dropChance;
    private int defence = 10;
    private float absorption = 0.9f;
    int level = 1;
    @Override
    public String getTitle() {
        return getTitle(false);
    }
    public String getTitle(boolean raw) {
        return title + (raw ? "" : " " + level);
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
    }
}
