package com.skyteeee.tungeon.storage;

import com.skyteeee.tungeon.entities.Enemy;
import com.skyteeee.tungeon.entities.Entity;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.items.Item;

import java.util.*;

public class Storage {

    static Storage instance = new Storage();
    public static Storage getInstance() {
        return instance;
    }

    int nextId = 1;

    int turn = 0;

    Map<Integer, Entity> entities = new HashMap<>();

    public void putEntity(Entity entity) {
        int id = entity.getId();
        if (entities.containsKey(id)) {
            throw new RuntimeException("Id " + id + " already exists. ");
        }
        entities.put(id, entity);
        if (id > nextId - 1) {
            nextId = id + 1;
        }
    }

    public int getTurn() {
        return turn;
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    public Path getPath(int id) {return (Path) getEntity(id);}

    public Place getPlace(int id) {return (Place) getEntity(id);}

    public Enemy getEnemy(int id) {
        return (Enemy) getEntity(id);
    }

    public Item getItem(int id) {return (Item) getEntity(id);}

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

    public void nextTurn() {
        turn++;
    }

    public void resetTurn() {
        turn = 0;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    public List<Place> getAllPlaces() {
        Collection<Entity> entities = getAllEntities();
        List<Place> places = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            if (entity instanceof Place place) {
                places.add(place);
            }
        }
        return places;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getId());
    }

}
