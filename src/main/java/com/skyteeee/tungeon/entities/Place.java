package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Place extends EntityClass {

    private List<Integer> paths = new ArrayList<>();

    private String description;
    private String title;

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
        return index < paths.size() && index >= 0 ? Storage.getInstance().getPath(paths.get(index)) : null;
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle());
        object.put("description", getDescription());
        JSONArray pathsArray = new JSONArray();
        for (int id : paths) {
            pathsArray.put(id);
        }
        object.put("paths", pathsArray);
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
        System.out.println("You are in " + description + " (" + getId() + ")");
        System.out.println("-----");
        System.out.println("You see the following paths: ");
        for (int i = 0; i < paths.size(); i ++) {
            Path path = (Path) Storage.getInstance().getEntity(paths.get(i));
            path.printState(i+1, player, this);
        }
    }


}
