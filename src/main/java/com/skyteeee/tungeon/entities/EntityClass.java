package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;

public abstract class EntityClass implements Entity {
    private int id;
    protected World world;

    public void setWorld(World w) {
        world = w;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }



}
