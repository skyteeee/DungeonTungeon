package com.skyteeee.tungeon.entities.items;

import com.skyteeee.tungeon.entities.Entity;
import com.skyteeee.tungeon.entities.Place;

public interface Item extends Entity {
    String getTitle();
    void setTitle(String title);

    void setDropChance(float chance);

    float getDropChance();

    void drop(Place place);

    void setDurability(float durability);

    float getDurability();

    void setResistance(float resistance);

    float getResistance();

    void applyDamage(int damage);

}
