package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.storage.Storage;

import java.util.ArrayList;
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

    public Path getPath (int index) {
        return index < paths.size() && index >= 0 ? Storage.getInstance().getPath(paths.get(index)) : null;
    }

    public void printState() {
        System.out.println("You are in " + description);
        System.out.println("-----");
        System.out.println("You see the following paths: ");
        for (int i = 0; i < paths.size(); i ++) {
            Path path = (Path) Storage.getInstance().getEntity(paths.get(i));
            System.out.println("" + (i+1) + ". " + path.getTitle());
        }
    }


}
