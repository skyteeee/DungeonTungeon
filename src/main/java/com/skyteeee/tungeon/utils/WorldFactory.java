package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.*;
import com.skyteeee.tungeon.entities.Character;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WorldFactory {

    public static final String FALLBACK_FILE_NAME = "fallback.json";
    public static final String SAVE_DIR = "save";
    /**
     * total amount of places in a world
     */
    int totalPlaces = 100;
    int maxPathsPerPlace = 4;
    EntityFactory factory = new EntityFactory();
    Storage storage = Storage.getInstance();
    List<Place> allPlaces = new LinkedList<>();
    List<Place> nextPlaces = new LinkedList<>();
    List<Place> availPlaces = new LinkedList<>();

    String loadedFrom = null;

    /**
     * Generates a new world by using a KWG (Khramov World Generator) algorithm to make a unique random world
     * with places and paths connecting them.
     * @return World: world which was just generated. All the entities have already been initialized and put into Storage
     */
    public World generate() {

        World world = newWorld();
        int newPlaceChance = 70;

        storage.clear();
        Place first = createPlace(world);
        while (allPlaces.size() < totalPlaces) {
            Place current;
            int newPaths;
            boolean forceNewPlace = false;
            if (!nextPlaces.isEmpty()) {
                current = nextPlaces.remove(0);
                int maxPaths = maxPathsPerPlace - current.getPathCount();
                newPaths = maxPaths == 0 ? 0 : EntityFactory.rnd.nextInt(maxPaths + 1);
            } else {
                //choose one of existing and always add new path & place
                if (!availPlaces.isEmpty()) {
                    current = getAvailablePlace(new LinkedList<>());
                } else {
                    current = allPlaces.get(allPlaces.size()-1);
                    Path pathToDelete = current.getPath(current.getPathCount()-1);
                    Place[] unlinked = pathToDelete.unlink();
                    availPlaces.add(unlinked[0] == current ? unlinked[1] : unlinked[0]);
                    storage.removeEntity(pathToDelete);
                }
                newPaths = 1;
                forceNewPlace = true;
            }

            if (newPaths > 0) {
                for (int i = 0; i < newPaths; i++) {
                    Path path = factory.createPath();
                    boolean needNewPlace = forceNewPlace || availPlaces.isEmpty() || EntityFactory.rnd.nextInt(100) <= newPlaceChance;
                    Place destination;
                    if (needNewPlace) {
                        destination = createPlace(world);
                    } else {
                        List<Place> skip = new LinkedList<>();
                        skip.add(current);
                        skip.addAll(current.getDestinations());
                        destination = getAvailablePlace(skip);
                        if (destination == null) {
                            destination = createPlace(world);
                        }
                    }

                    path.setPlaces(current, destination);
                    if (destination.getPathCount() >= maxPathsPerPlace) {
                        removeFromAvail(destination);
                    }
                }
            }

            if (current.getPathCount() >= maxPathsPerPlace) {
                removeFromAvail(current);
            }


        }



        world.setTotalPlaces(totalPlaces);
        world.setSpawn(first);
        Player player = factory.createPlayer();
        world.setPlayer(player);
        player.setCurrentPlace(first);
        first.addPlayer(player);
        player.setArmor(factory.createArmor(1));
        return world;
    }

    public World newWorld() {
        return new World(factory);
    }


    private void removeFromAvail(Place place) {
        availPlaces.remove(place);
    }

    private Place getAvailablePlace(List<Place> skip) {
        while (true) {
            int idx = EntityFactory.rnd.nextInt(availPlaces.size());
            Place chosen = availPlaces.get(idx);
            if (!skip.contains(chosen)) {
                return chosen;
            }
            if (availPlaces.size() == skip.size()) {
                return null;
            }
        }
    }

    private Place createPlace(World world) {
        Place place = factory.createPlace(world);
        allPlaces.add(place);
        nextPlaces.add(place);
        availPlaces.add(place);
        return place;
    }

    public boolean save(World world, String fileNameString) {
        JSONObject saveObject = new JSONObject();
        JSONObject worldObject = world.serialize();
        JSONArray placesArray = new JSONArray();
        JSONArray pathsArray = new JSONArray();
        JSONArray playersArray = new JSONArray();
        JSONArray weaponsArray = new JSONArray();
        JSONArray armorArray = new JSONArray();
        JSONArray enemiesArray = new JSONArray();

        String fileName = fileNameString == null ? (loadedFrom == null ? FALLBACK_FILE_NAME : loadedFrom): fileNameString;

        Collection<Entity> entities = storage.getAllEntities();

        for (Entity entity : entities) {
            if (entity instanceof Place) {
                placesArray.put(entity.serialize());
            }
            if (entity instanceof Path) {
                pathsArray.put(entity.serialize());
            }
            if (entity instanceof Player) {
                playersArray.put(entity.serialize());
            }
            if (entity instanceof Weapon) {
                weaponsArray.put(entity.serialize());
            }
            if (entity instanceof Enemy) {
                enemiesArray.put(entity.serialize());
            }
            if (entity instanceof Armor) {
                armorArray.put(entity.serialize());
            }

        }

        worldObject.put("places", placesArray);
        worldObject.put("paths", pathsArray);
        worldObject.put("players", playersArray);
        worldObject.put("weapons", weaponsArray);
        worldObject.put("enemies", enemiesArray);
        worldObject.put("armor", armorArray);
        worldObject.put("turn", storage.getTurn());

        saveObject.put("world", worldObject);
        String toSave = saveObject.toString(2);

        java.nio.file.Path currentPath = Paths.get(SAVE_DIR);
        if (!Files.exists(currentPath)) {
            try {
                Files.createDirectory(currentPath);
            } catch (IOException exception) {
                return false;
            }
        }

        java.nio.file.Path savePath = Paths.get(SAVE_DIR, fileName);

        try {
            Files.writeString(savePath, toSave, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return false;
        }

        System.out.println("Successfully saved to " + savePath.toAbsolutePath());
        return true;
    }

    public World load(String fileNameString) {

        String fileName = fileNameString == null ? FALLBACK_FILE_NAME : fileNameString;
        java.nio.file.Path loadPath = Paths.get(SAVE_DIR, fileName);
        String fileContents = null;
        try {
            fileContents = Files.readString(loadPath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.out.println("Could not load world from " + loadPath.toAbsolutePath());
            return null;
        }
        storage.clear();

        World world = newWorld();

        JSONObject bigObject = new JSONObject(fileContents);
        JSONObject worldObject = bigObject.getJSONObject("world");
        JSONArray placesArray = worldObject.getJSONArray("places");
        JSONArray pathsArray = worldObject.getJSONArray("paths");
        JSONArray playersArray = worldObject.getJSONArray("players");
        JSONArray weaponsArray = worldObject.getJSONArray("weapons");
        JSONArray enemiesArray = worldObject.optJSONArray("enemies", new JSONArray());
        JSONArray armorArray = worldObject.optJSONArray("armor", new JSONArray());
        storage.setTurn(worldObject.optInt("turn", 0));

        for (int i = 0; i < enemiesArray.length(); i++) {
            JSONObject enemyObject = enemiesArray.getJSONObject(i);
            Enemy enemy = factory.newEnemy(1);
            enemy.deserialize(enemyObject);
            storage.putEntity(enemy);
        }

        for (int i = 0; i < armorArray.length(); i++) {
            JSONObject armorObject = armorArray.getJSONObject(i);
            Armor armor = factory.newArmor();
            armor.deserialize(armorObject);
            storage.putEntity(armor);
        }

        for (int i = 0; i < placesArray.length(); i ++) {
            JSONObject placeObject = placesArray.getJSONObject(i);
            Place place = factory.newPlace();
            place.deserialize(placeObject);
            place.setWorld(world);
            storage.putEntity(place);
        }

        for (int i = 0; i < pathsArray.length(); i ++) {
            JSONObject pathObject = pathsArray.getJSONObject(i);
            Path path = factory.newPath();
            path.deserialize(pathObject);
            storage.putEntity(path);
        }

        for (int i = 0; i < weaponsArray.length(); i ++) {
            JSONObject weaponsObject = weaponsArray.getJSONObject(i);
            Weapon weapon = factory.newWeapon();
            weapon.deserialize(weaponsObject);
            storage.putEntity(weapon);
        }


        for (int i = 0; i < playersArray.length(); i ++) {
            JSONObject playerObject = playersArray.getJSONObject(i);
            Player player = factory.newPlayer();
            player.deserialize(playerObject);
            storage.putEntity(player);
            world.setPlayer(player);
        }

        world.deserialize(worldObject);
        world.setTotalPlaces(placesArray.length());

        loadedFrom = fileNameString;

        return world;
    }

}
