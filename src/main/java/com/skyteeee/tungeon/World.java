package com.skyteeee.tungeon;

import com.skyteeee.tungeon.entities.Enemy;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.*;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.List;

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

    private void nextTurn() {
        Storage instance = Storage.getInstance();
        List<Enemy> enemies = instance.getAllOfType(Enemy.class);
        List<Player> players = instance.getAllOfType(Player.class);
        for (Enemy enemy : enemies) {
            enemy.onTurn();
        }
        for (Player playa : players) {
            playa.onTurn();
        }
        Storage.getInstance().nextTurn();
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
        nextTurn();
    }

    public void onPlayerDeath() {
        player.resurrect(getSpawn());
        Storage.getInstance().resetTurn();
    }

    public boolean move(int choice) {
        Place place = player.getCurrentPlace();
        Path path = place.getPath(choice-1);
        if (path != null) {
            boolean attacked = false;
            for (int i = 0; i < place.getEnemyAmount(); i++) {
                Enemy enemy = place.getEnemy(i);
                if (enemy.willAttack()) {
                    enemy.attack(player, null);
                    attacked = true;
                }
            }
            if (!attacked) {
                Place destination = path.getDestination(place);
                path.addVisitor(player);
                player.setCurrentPlace(destination);
                destination.addPlayer(player);
                place.removePlayer(player);
            }
            nextTurn();
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.setWorld(this);
    }

    public void onLevelUp(int level) {
        int enemiesToMake = (int)(totalPlaces * 0.2 + level * 0.2);
        int weaponsToMake = (int)(totalPlaces * 0.2 + level * 0.15);
        int armorsToMake = (int)(totalPlaces * 0.2 + level * 0.1);
        Place exclude = player.getCurrentPlace();

        factory.scatterEnemies((int)(enemiesToMake * 0.15), level-1, exclude);
        factory.scatterEnemies((int)(enemiesToMake * 0.55), level, exclude);
        factory.scatterEnemies((int)(enemiesToMake * 0.30), level+1, exclude);


        factory.scatterWeapons((int)(weaponsToMake * 0.25), level-1, exclude);
        factory.scatterWeapons((int)(weaponsToMake * 0.55), level, exclude);
        factory.scatterWeapons((int)(weaponsToMake * 0.2), level+1, exclude);

        factory.scatterArmor((int)(armorsToMake * 0.3), level-1, exclude);
        factory.scatterArmor((int)(armorsToMake * 0.55), level, exclude);
        factory.scatterArmor((int)(armorsToMake * 0.15), level+1, exclude);
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
