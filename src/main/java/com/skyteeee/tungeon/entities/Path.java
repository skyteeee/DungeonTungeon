package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Path extends EntityClass {

    private int[] places = new int[2];

    private Set<Integer> playersVisited = new HashSet<>();

    private String title;

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle());
        JSONArray places = new JSONArray();
        for (int i : this.places) {
            places.put(i);
        }
        object.put("places", places);
        JSONArray visitors = new JSONArray();
        for (int i : this.playersVisited) {
            visitors.put(i);
        }
        object.put("visitors", visitors);
        return object;
    }


    /**
     * Deserializes from a JSONObject to load from save file
     *
     * @param object JSONObject containing path properties
     */
    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        JSONArray places = object.getJSONArray("places");
        for (int i = 0; i < places.length(); i++) {
            int id = places.getInt(i);
            this.places[i] = id;
        }
        JSONArray visitors = object.getJSONArray("visitors");
        for (int i = 0; i < visitors.length(); i++) {
            int id = visitors.getInt(i);
            playersVisited.add(id);
        }
    }

    public void setPlaces(Place p1, Place p2) {
        places[0] = p1.getId();
        places[1] = p2.getId();
        p1.addPath(this);
        p2.addPath(this);
    }

    public void addVisitor(Character character) {
        playersVisited.add(character.getId());
    }

    public Place getDestination(Place location) {
        int locationId = location.getId();
        int destinationId = locationId == places[0] ? places[1] : places[0];
        return Storage.getInstance().getPlace(destinationId);
    }

    public Place[] unlink() {
        Place[] unlinked = new Place[places.length];
        Storage storage = Storage.getInstance();
        unlinked[0] = storage.getPlace(places[0]);
        unlinked[1] = storage.getPlace(places[1]);
        unlinked[0].removePath(this);
        unlinked[1].removePath(this);
        return unlinked;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean hasVisited(Character character) {
        return playersVisited.contains(character.getId());
    }

    public void printState(int index, Player player, Place current) {
        if (hasVisited(player)) {
            System.out.printf("%d. %s -> %s\n", index, getTitle(), getDestination(current).getTitle());

        } else {
            System.out.printf("%d. %s\n", index, getTitle());
        }
    }

    public String getTitle() {
        return title;
    }

}
