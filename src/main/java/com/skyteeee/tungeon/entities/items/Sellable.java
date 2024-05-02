package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.EntityClass;
import com.skyteeee.tungeon.entities.Place;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Sellable extends EntityClass implements Item{
    String title;
    int itemId;
    private Map<String,String> properties = new HashMap<>();
    private int price;

    public void setProperty(String key, String value) {
        properties.put(key,value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDropChance(float chance) {

    }

    @Override
    public float getDropChance() {
        return 0;
    }

    @Override
    public void drop(Place place) {

    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        JSONObject prop = new JSONObject(properties);
        object.put("id", getId());
        object.put("props",prop);
        object.put("title", getTitle());
        object.put("itemId", getItemId());
        object.put("price", getPrice());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setItemId(object.getInt("itemId"));
        setPrice(object.getInt("price"));
        JSONObject props = object.optJSONObject("props", new JSONObject());
        for (String key : props.keySet()) {
            properties.put(key, props.getString(key));
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
