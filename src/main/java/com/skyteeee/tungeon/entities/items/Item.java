package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.Entity;
import com.skyteeee.tungeon.entities.Place;
import org.json.JSONObject;

public interface Item extends Entity {
    String getTitle();
    void setTitle(String title);

    void setDropChance(float chance);

    float getDropChance();

    void drop(Place place);

}
