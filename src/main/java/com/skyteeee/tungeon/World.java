package com.skyteeee.tungeon;

import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.GameObject;
import com.skyteeee.tungeon.utils.WorldFactory;

import java.util.Map;
import java.util.Scanner;

public class World implements GameObject {
    private Player player;

    public void createWorld() {
        EntityFactory factory = new EntityFactory();
        Place p1 = factory.createPlace();
        Place p2 = factory.createPlace();
        Path path1 = factory.createPath();
        path1.setPlaces(p1,p2);
        Place p3 = factory.createPlace();
        Path path2 = factory.createPath();
        path2.setPlaces(p1, p3);

        player.setCurrentPlace(p1);
    }

    public Player getPlayer() {
        return player;
    }

    public void printState() {
        System.out.println("\n-----");
        player.getCurrentPlace().printState(player);
    }

    public void give(int choice) {
        player.take(choice);
    }

    public boolean processInput(int choice) {
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

    public void setPlayer(Player player) {this.player = player;}
}
