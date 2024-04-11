package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;

public class AwaitingAttackCommand extends AwaitingCommand{
    int enemyIdx;
    public AwaitingAttackCommand(int enemyIdx) {
        this.enemyIdx = enemyIdx;
    }
    @Override
    public void process(World world, int choice) {
        if (world.attack(enemyIdx, choice)) {
            world.printState();
        }
    }
}
