package com.skyteeee.tungeon;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        World world = new World();
        world.createWorld();
        gameLoop(world);

    }

    private static void gameLoop(World world) {
        while (true) {
            world.printState();
            world.userInput();
        }

    }



}