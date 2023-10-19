package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.storage.Storage;

import java.util.*;

public class WorldFactory {
    int totalPlaces = 10;
    int maxPathsPerPlace = 3;
    EntityFactory factory = new EntityFactory();
    Storage storage = Storage.getInstance();
    List<Place> allPlaces = new LinkedList<>();
    List<Place> nextPlaces = new LinkedList<>();
    List<Place> availPlaces = new LinkedList<>();

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

}
