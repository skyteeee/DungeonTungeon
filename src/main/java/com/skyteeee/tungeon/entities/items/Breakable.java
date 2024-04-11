package com.skyteeee.tungeon.entities.items;

public interface Breakable extends Item{

    void setDurability(float durability);

    float getDurability();

    void setResistance(float resistance);

    float getResistance();

    void applyDamage(int damage);
}
