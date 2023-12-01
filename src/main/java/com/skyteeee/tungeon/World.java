package com.skyteeee.tungeon;

import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.*;
import org.json.JSONObject;

public class World implements GameObject, Savable {
    private Player player;
    private int spawnId;
    private EntityFactory factory;
    private int totalPlaces = 10;

    public World (EntityFactory factory) {
        this.factory = factory;
    }

    public void setTotalPlaces(int places) {
        totalPlaces = places;
    }

    public Player getPlayer() {
        return player;
    }
    public void setSpawn(int id) {
        spawnId = id;
    }

    public void setSpawn(Place place) {
        setSpawn(place.getId());
    }

    public Place getSpawn() {
        return Storage.getInstance().getPlace(spawnId);
    }

    public void printState() {
        System.out.println();
        UserInterface.strike();
        player.getCurrentPlace().printState(player);
    }

    public void give(int choice) {
        player.take(choice);
    }

    public void take(int choice) {
        Item item = player.give(choice);
        player.getCurrentPlace().take(item);
    }

    public void attack(int enemyIdx, UserInterface ui) {
        player.attack(enemyIdx, ui);
        if (player.isDead()) {
            player.resurrect(getSpawn());
        }
    }

    public boolean move(int choice) {
        Place place = player.getCurrentPlace();
        Path path = place.getPath(choice-1);
        if (path != null) {
            Place destination = path.getDestination(place);
            path.addVisitor(player);
            player.setCurrentPlace(destination);
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.setWorld(this);
    }

    public void onLevelUp(int level) {
        int enemiesToMake = (int)(totalPlaces * 0.1 + level * 0.2);
        Place exclude = player.getCurrentPlace();
        factory.createEnemies((int)(enemiesToMake * 0.25), level-1, exclude);
        factory.createEnemies((int)(enemiesToMake * 0.55), level, exclude);
        factory.createEnemies((int)(enemiesToMake * 0.2), level+1, exclude);
    }


    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("spawn", spawnId);
        object.put("player", player.getId());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setSpawn(object.getInt("spawn"));
        int playerId = object.optInt("player", 0);
        if (playerId != 0) {
            setPlayer((Player) Storage.getInstance().getEntity(playerId));
        }
    }
}
