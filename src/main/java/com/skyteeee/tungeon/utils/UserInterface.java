package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.Main;
import com.skyteeee.tungeon.World;

import java.util.Scanner;

public class UserInterface {
    private final Scanner inputScanner = new Scanner(System.in);
    private final WorldFactory worldFactory;
    private World currentWorld;

    public UserInterface (WorldFactory factory) {
        worldFactory = factory;
    }

     public void initialMessage() {
        System.out.println("Welcome to the Dungeon Tungeon!");
        System.out.println("type '/new' for new game");
        System.out.println("type '/load [file name]' to load old save");
        System.out.println("type '/exit' to quit");
        System.out.println("at any point, type '/help' for a list of commands");


        userInput("");
    }

    public void printState() {
        currentWorld.printState();
    }

    public void userInput() {
        userInput("Where would you like to go? ");
    }

    public void userInput(String prompt) {
        while (true) {
            System.out.print(prompt + "> ");

            try {
                int choice = inputScanner.nextInt();

                if (processInput(choice)) {
                    break;
                }

                System.out.println("Sorry, please type in a valid option.");

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

    private boolean processInput(int choice) {
        if (currentWorld == null) {
            return false;
        }
        return currentWorld.processInput(choice);
    }

    private boolean save(String fileName) {
        return currentWorld != null && worldFactory.save(currentWorld, fileName);
    }

    private boolean processCommand(String command) {
        if (command.startsWith("/")) {
            String[] parts = command.split(" ");
            String commandCore = parts[0];
            switch (commandCore) {
                case "/exit" : {
                    save(WorldFactory.FALLBACK_FILE_NAME);
                    Main.isRunning = false;
                }
                break;

                case "/save" : {
                    return save(parts.length > 1 ? parts[1] : null);
                }

                case "/new" : {
                    newWorld();
                }
                break;

                case "/load" :  {
                    return loadWorld(parts.length > 1 ? parts[1] : null);
                }

                default: {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean loadWorld(String fileName) {
        currentWorld = worldFactory.load(fileName);
        return currentWorld != null;
    }

    private void newWorld() {
        currentWorld = worldFactory.generate();
    }


}
