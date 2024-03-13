package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.World;

public class UIOutput {

    public UIOutput() {
    }

    public void strike() {
        println("-----");
    }



    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {

        }
    }

    public void slowPrint(String message) {
        slowPrint(message, 40);
    }

    public void slowPrint(String message, long delay) {
        char[] chars = message.toCharArray();
        for (char c : chars) {
            print(String.valueOf(c));
            sleep(delay);
        }
    }

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void println() {
        System.out.println();
    }

}
