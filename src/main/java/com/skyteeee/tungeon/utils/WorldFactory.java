package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.Entity;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WorldFactory {
    int totalPlaces = 10;
    int maxPathsPerPlace = 3;
    EntityFactory factory = new EntityFactory();
    Storage storage = Storage.getInstance();
    List<Place> allPlaces = new LinkedList<>();
    List<Place> nextPlaces = new LinkedList<>();
    List<Place> availPlaces = new LinkedList<>();

    /**
     * Generates a new world by using a KWG (Khramov World Generator) algorithm to make a unique random world
     * with places and paths connecting them.
     * @return World: world which was just generated. All the entities have already been initialized and put into Storage
     */
    public World generate() {

        int newPlaceChance = 60;


        Place first = createPlace();
        while (allPlaces.size() < totalPlaces) {
            Place current;
            int newPaths;
            boolean forceNewPlace = false;
            if (!nextPlaces.isEmpty()) {
                current = nextPlaces.remove(0);
                int maxPaths = maxPathsPerPlace - current.getPathCount();
                newPaths = maxPaths == 0 ? 0 : factory.rnd.nextInt(maxPaths + 1);
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
                    boolean needNewPlace = forceNewPlace || availPlaces.isEmpty() || factory.rnd.nextInt(100) <= newPlaceChance;
                    Place destination;
                    if (needNewPlace) {
                        destination = createPlace();
                    } else {
                        List<Place> skip = new LinkedList<>();
                        skip.add(current);
                        skip.addAll(current.getDestinations());
                        destination = getAvailablePlace(skip);
                        if (destination == null) {
                            destination = createPlace();
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


        World world = new World();
        world.setPlayer(factory.createPlayer());
        world.getPlayer().setCurrentPlace(first);
        return world;
    }

    private void removeFromAvail(Place place) {
        availPlaces.remove(place);
    }

    private Place getAvailablePlace(List<Place> skip) {
        while (true) {
            int idx = factory.rnd.nextInt(availPlaces.size());
            Place chosen = availPlaces.get(idx);
            if (!skip.contains(chosen)) {
                return chosen;
            }
            if (availPlaces.size() == skip.size()) {
                return null;
            }
        }
    }

    private Place createPlace() {
        Place place = factory.createPlace();
        allPlaces.add(place);
        nextPlaces.add(place);
        availPlaces.add(place);
        return place;
    }

    public boolean save(World world, String fileNameString) {
        JSONObject saveObject = new JSONObject();
        JSONObject worldObject = new JSONObject();
        JSONArray placesArray = new JSONArray();
        JSONArray pathsArray = new JSONArray();
        JSONArray playersArray = new JSONArray();

        String fileName = fileNameString == null ? "fallback.json" : fileNameString;

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
        }

        worldObject.put("places", placesArray);
        worldObject.put("paths", pathsArray);
        worldObject.put("players", playersArray);

        saveObject.put("world", worldObject);
        String toSave = saveObject.toString(2);

        java.nio.file.Path currentPath = Paths.get("save");
        if (!Files.exists(currentPath)) {
            try {
                Files.createDirectory(currentPath);
            } catch (IOException exception) {
                return false;
            }
        }

        java.nio.file.Path savePath = Paths.get("save", fileName);

        try {
            Files.writeString(savePath, toSave, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return false;
        }


        return true;
    }



}
