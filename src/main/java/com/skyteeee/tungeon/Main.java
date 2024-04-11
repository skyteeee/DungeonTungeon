package com.skyteeee.tungeon;

import com.skyteeee.tungeon.telebot.BotoBatya;
import com.skyteeee.tungeon.utils.UserInterface;
import com.skyteeee.tungeon.utils.WorldFactory;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static boolean isRunning = true;

    public static void main(String[] args) {
        WorldFactory worldFactory = new WorldFactory();
        UserInterface userInterface = new UserInterface(worldFactory);
        BotoBatya batya = new BotoBatya();
        batya.setup(args[0]);
        userInterface.initialMessage();
        gameLoop(userInterface);

    }

    private static void gameLoop(UserInterface userInterface) {
        while (isRunning) {
            userInterface.printState();
            userInterface.userInput();
        }

    }



}