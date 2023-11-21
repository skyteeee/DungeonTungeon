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

    private static final String INVALID_OPTION_MESSAGE = "Sorry, please type in a valid option.";

    public static void strike() {
        System.out.println("-----");
    }

     public void initialMessage() {
        slowPrint("Welcome to the Dungeon Tungeon!\n", 40);
        slowPrint("type '/new' for new game\n", 40);
        slowPrint("type '/load [file name]' to load old save\n", 40);
        slowPrint("type '/exit' to quit\n", 40);
        slowPrint("at any point, type '/help' for a list of commands\n", 40);


        userInput("");
    }

    public void printState() {
        UserInterface.slowPrint("\n\n\n", 250);
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

                System.out.println(INVALID_OPTION_MESSAGE);

            } catch (Exception exception) {
                String command = inputScanner.nextLine();
                if (!processCommand(command)) {
                    System.out.println(INVALID_OPTION_MESSAGE);
                } else {
                    break;
                }

            }
        }

    }

    public int inputChoice(String prompt, int bound) {
        while (true) {
            System.out.print(prompt + "> ");

            try {
                int choice = inputScanner.nextInt();
                if (choice <= bound && choice > 0) {
                    return choice;
                }
                System.out.println(INVALID_OPTION_MESSAGE);
            } catch (Exception exception) {
                inputScanner.nextLine();
                System.out.println(INVALID_OPTION_MESSAGE);
            }
        }
    }

    private boolean processInput(int choice) {
        if (currentWorld == null) {
            return false;
        }
        return currentWorld.move(choice);
    }

    private boolean save(String fileName) {
        return currentWorld != null && worldFactory.save(currentWorld, fileName);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {

        }
    }

    public static void slowPrint(String message) {
        slowPrint(message, 40);
    }

    public static void slowPrint(String message, long delay) {
        char[] chars = message.toCharArray();
        for (char c : chars) {
            System.out.print(c);
            sleep(delay);
        }
    }

    private boolean processCommand(String command) {
        if (command.startsWith("/")) {
            String[] parts = command.split(" ");
            String commandCore = parts[0];
            try {


                switch (commandCore) {
                    case "/exit": {
                        save(WorldFactory.FALLBACK_FILE_NAME);
                        Main.isRunning = false;
                    }
                    break;

                    case "/save": {
                        return save(parts.length > 1 ? parts[1] : null);
                    }

                    case "/new": {
                        newWorld();
                    }
                    break;

                    case "/load": {
                        return loadWorld(parts.length > 1 ? parts[1] : null);
                    }

                    case "/inv": {
                        if (currentWorld == null) {
                            return false;
                        }
                        currentWorld.getPlayer().printInventory();
                    }
                    break;

                    case "/drop": {
                        if (parts.length == 1) {
                            return false;
                        }

                        currentWorld.take(Integer.parseInt(parts[1]) - 1);

                    }
                    break;

                    case "/take": {
                        if (parts.length == 1) {
                            return false;
                        }

                        currentWorld.give(Integer.parseInt(parts[1]) - 1);

                    }
                    break;

                    case "/attack": {
                        int choice = parts.length == 1 ? 1 : Integer.parseInt(parts[1]);
                        currentWorld.attack(choice - 1, this);
                    }
                    break;

                    case "/status": {
                        currentWorld.getPlayer().printState();
                    }
                    break;

                    default: {
                        return false;
                    }
                }
            }  catch (IndexOutOfBoundsException exception) {
                System.out.println("Index outside of option choices. ");
                return false;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a number as your choice.");
                return false;
            } catch (NullPointerException exception) {
                return false;
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
