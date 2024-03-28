package com.skyteeee.tungeon.utils;

import com.vdurmont.emoji.EmojiParser;

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
    public void sleep(long millis) {

    }

    @Override
    public void println() {
        buffer.append("\n");
    }
    @Override
    public String flush() {
        String str = EmojiParser.parseToUnicode(buffer.toString());
        buffer = new StringBuilder();
        return str;
    }

}
