package com.skyteeee.tungeon.entities;

public abstract class EntityClass implements Entity {
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }



}
