package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;

public class AwaitingBuyCommand extends AwaitingCommand{
    @Override
    public void process(World world, int choice) {
        world.buy(choice);
        world.printState();
    }
}
