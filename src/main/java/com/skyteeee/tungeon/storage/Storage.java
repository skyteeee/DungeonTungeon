package com.skyteeee.tungeon.storage;

import com.skyteeee.tungeon.entities.Entity;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {

    static Storage instance = new Storage();
    public static Storage getInstance() {
        return instance;
    }

    int nextId = 1;

    Map<Integer, Entity> entities = new HashMap<>();

    public void putEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    public Path getPath(int id) {return (Path) getEntity(id);}

    public Place getPlace(int id) {return (Place) getEntity(id);}

    public int getNextId() {
        return nextId++;
    }

    public void addNewEntity(Entity entity) {
        entity.setId(getNextId());
        putEntity(entity);
    }

    public void clear() {
        entities = new HashMap<>();
        nextId = 1;
    }

    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getId());
    }

}
