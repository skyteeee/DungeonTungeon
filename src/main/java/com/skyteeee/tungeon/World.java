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
    private Scanner inputScanner = new Scanner(System.in);

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

    public void setPlayer(Player player) {this.player = player;}

    public void printState() {
        System.out.println("\n-----");
        player.getCurrentPlace().printState(player);
    }

    private boolean processInput(int choice) {
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

    public void userInput() {
        while (true) {
            System.out.print("Where would you like to go? > ");

            try {
                int choice = inputScanner.nextInt();

                if (processInput(choice)) {
                    break;
                }

                System.out.println("Sorry, invalid choice number.");

            } catch (Exception exception) {
                String command = inputScanner.nextLine();
                if (!processCommand(command)) {
                    System.out.println("Sorry, please type in a valid option.");
                } else {
                    break;
                }

            }
        }

    }

    private boolean save(String fileName) {
        WorldFactory factory = new WorldFactory();
        return factory.save(this, fileName);
    }

    private boolean processCommand(String command) {
        if (command.startsWith("/")) {
            String[] parts = command.split(" ");
            String commandCore = parts[0];
            switch (commandCore) {
                case "/exit" : {
                    save(parts.length > 1 ? parts[1] : null);
                    Main.isRunning = false;
                }
                break;

                case "/save" : {
                    save(parts.length > 1 ? parts[1] : null);
                }
                break;
            }
            return true;
        }

        return false;
    }

}
