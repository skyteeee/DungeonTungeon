package com.skyteeee.tungeon;

import com.skyteeee.tungeon.utils.WorldFactory;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static boolean isRunning = true;
    public static void main(String[] args) {
        WorldFactory worldFactory = new WorldFactory();
        World world = worldFactory.generate();
        System.out.println(worldFactory.save(world, "world1.json"));
        gameLoop(world);

    }

    private static void gameLoop(World world) {
        while (isRunning) {
            world.printState();
            world.userInput();
        }

    }



}