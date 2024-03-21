package com.skyteeee.tungeon.utils;

public class UITelegramOutput extends UIOutput{

    private StringBuilder buffer = new StringBuilder();

    @Override
    public void strike() {
        buffer.append("-----\n");
    }

    @Override
    public void slowPrint(String message) {
        buffer.append(message);
    }

    @Override
    public void slowPrint(String message, long delay) {
        buffer.append(message);
    }

    @Override
    public void print(String message) {
        buffer.append(message);
    }

    @Override
    public void println(String message) {
        buffer.append(message);
        buffer.append("\n");
    }

    @Override
    public void println() {
        buffer.append("\n");
    }
    @Override
    public String flush() {
        String str = buffer.toString();
        buffer = new StringBuilder();
        return str;
    }

}
