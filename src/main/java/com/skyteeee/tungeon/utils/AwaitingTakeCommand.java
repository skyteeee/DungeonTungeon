package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;

public class AwaitingTakeCommand extends AwaitingCommand{
    @Override
    public void process(World world, int choice) {
        try {
            world.give(choice);
            world.printState();
        } catch (Exception e) {
            world.getUi().println(":rage: Wrong choice! Please choose an item that exists.");
            world.give();
        }
    }
}
