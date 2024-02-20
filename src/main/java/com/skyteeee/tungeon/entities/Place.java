package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Place extends EntityClass {

    private List<Integer> paths = new ArrayList<>();
    private List<Integer> enemies = new ArrayList<>();
    private List<Integer> players = new ArrayList<>();

    private String description;
    private String title;

    private final Inventory inventory = new Inventory(10);

    public Item give(int choice) {
        Item item = inventory.getItem(choice);
        inventory.removeItem(item);
        return item;
    }

    public void take(Item item) {
        inventory.addItem(item);
    }
    public Inventory getInventory() {
        return inventory;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {return description;}

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {return title;}

    public void addPath(Path path) {
        paths.add(path.getId());
    }

    public void addPath(int id) {
        paths.add(id);
    }

    public Path getPath (int index) {
        return index < paths.size() && index >= 0 ? Storage.getInstance().getOfType(paths.get(index), Path.class) : null;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy.getId());
    }

    public void addEnemy(int id) {
        enemies.add(id);
    }

    public Enemy getEnemy(int index) {
        return Storage.getInstance().getOfType(enemies.get(index), Enemy.class);
    }

    public int getEnemyAmount() {
        return enemies.size();
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove((Integer) enemy.getId());
    }

    public void addPlayer(Player player) {
        players.add(player.getId());
    }

    public void addPlayer(int id) {players.add(id);}

    public void removePlayer(Player player) {
        players.remove((Integer) player.getId());
    }

    public int getPlayerAmount() {
        return players.size();
    }

    public Player getPlayer(int index) {
        return Storage.getInstance().getOfType(players.get(index), Player.class);
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle());
        object.put("description", getDescription());

        JSONArray enemiesArray = new JSONArray();
        for (int id : enemies) {
            enemiesArray.put(id);
        }
        object.put("enemies", enemiesArray);

        JSONArray playersArray = new JSONArray();
        for (int id : players) {
            playersArray.put(id);
        }
        object.put("players", playersArray);

        JSONArray pathsArray = new JSONArray();
        for (int id : paths) {
            pathsArray.put(id);
        }
        object.put("paths", pathsArray);
        object.put("inventory", inventory.serialize());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setDescription(object.getString("description"));
        JSONArray pathsArray = object.getJSONArray("paths");
        for (int i = 0; i < pathsArray.length(); i++) {
            addPath(pathsArray.getInt(i));
        }
        JSONArray enemiesArray = object.optJSONArray("enemies", new JSONArray());
        for (int i = 0; i < enemiesArray.length(); i++) {
            addEnemy(enemiesArray.getInt(i));
        }

        JSONArray playersArray = object.optJSONArray("players", new JSONArray());
        for (int i = 0; i < playersArray.length(); i++) {
            addPlayer(playersArray.getInt(i));
        }

        inventory.deserialize(object.getJSONObject("inventory"));

    }

    public List<Place> getDestinations() {
        List<Place> destinations = new LinkedList<>();
        for (int id : paths) {
            Path path = Storage.getInstance().getPath(id);
            destinations.add(path.getDestination(this));
        }
        return destinations;
    }

    public void removePath(Path path) {
        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i) == path.getId()) {
                paths.remove(i);
                break;
            }
        }
    }

    public int getPathCount() {
         return paths.size();
    }

    public void printState(Player player) {
        Storage storage = Storage.getInstance();
        System.out.println("You are in " + description + " (" + getId() + ")");
        UserInterface.strike();
        if (!inventory.isEmpty()) {
            System.out.println("You see the following items: ");
            inventory.printState(false);
            UserInterface.strike();
        }

        if (!enemies.isEmpty()) {
            System.out.println("YOU ENCOUNTERED THE FOLLOWING ENEMIES: ");
            for (int i = 0; i < enemies.size(); i++) {
                int id = enemies.get(i);
                System.out.print((i+1) + ". ");
                storage.getEnemy(id).printState();
            }
            UserInterface.strike();
        }

        System.out.println("You see the following paths: ");
        for (int i = 0; i < paths.size(); i ++) {
            Path path = (Path) storage.getEntity(paths.get(i));
            path.printState(i+1, player, this);
        }
    }


}
