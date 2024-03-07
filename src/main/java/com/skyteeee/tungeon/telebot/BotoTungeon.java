package com.skyteeee.tungeon.telebot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotoTungeon extends TelegramLongPollingCommandBot {

    private String name;

    @Override
    public void onRegister() {
        super.onRegister();
        System.out.println("Registered Boto into Telegram");
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }

    @Override
    public String getBotUsername() {
        return name;
    }

    public BotoTungeon(String token, String name) {
        super(token);
        this.name = name;
        register(new HelloCommand());
    }

    class HelloCommand implements IBotCommand {
        @Override
        public String getCommandIdentifier() {
            return "hello";
        }

        @Override
        public String getDescription() {
            return "say hello to Boto!";
        }

        @Override
        public void processMessage(AbsSender absSender, Message message, String[] arguments) {
            String name = message.getFrom().getFirstName();
            SendMessage response = new SendMessage(message.getChatId().toString(), "Hello, " + name);
            try {
                absSender.execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
