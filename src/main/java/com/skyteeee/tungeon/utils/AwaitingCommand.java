package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;

public abstract class AwaitingCommand {
    public abstract void process(World world, int choice);
}
