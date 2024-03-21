package com.skyteeee.tungeon.telebot;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class BotoTungeon extends TelegramLongPollingCommandBot {

    private String name;
    private String password;
    private Set<Long> whitelist = new HashSet<>();
    private BotoBatya father;

    private Map<Long, World> worlds = new HashMap<>();
    private final UIOutput out = new UIOutput();

    @Override
    public void onRegister() {
        super.onRegister();
        out.println("Registered Boto into Telegram");
    }

    private void sendMessage(long chatId, String toSend) {
        SendMessage out = new SendMessage(String.valueOf(chatId), toSend);
        try {
            execute(out);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String contents = message.getText();
            World world = worlds.get(message.getChatId());
            if (world != null && world.getAwaitingCommand() != null) {
                AwaitingCommand command = world.getAwaitingCommand();
                try {
                    command.process(world, Integer.parseInt(contents));
                    sendMessage(message.getChatId(),world.getUi().flush());
                } catch (Exception e) {
                    sendMessage(message.getChatId(), "\uD83E\uDD21 We wanted a number...");
                }

            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    public BotoTungeon(String token, String name, String password, BotoBatya father) {
        super(token);
        this.father = father;
        this.name = name;
        this.password = password;
        loadWhitelist(father.getConfig());
        register(new HelloCommand());
        register(new StartCommand());
        register(new NewGameCommand());
        register(new InventoryCommand());
        register(new StatusCommand());
        register(new AttackCommand());
        for (int i = 0; i < WorldFactory.maxPathsPerPlace; i++) {
            register(new GoCommand(i+1));
        }
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
            UserInterface.sendTelegramMessage(message.getChatId(), "Hello, " + name, absSender);
        }
    }

    class StartCommand implements IBotCommand {
        @Override
        public String getCommandIdentifier() {
            return "start";
        }

        @Override
        public String getDescription() {
            return "connects boto to chat (password needed)";
        }

        @Override
        public void processMessage(AbsSender absSender, Message message, String[] arguments) {

            long chatId = message.getChatId();
            if (arguments.length != 0 && arguments[0].equals(password)) {
                addToWhitelist(chatId);
                UserInterface.sendTelegramMessage(chatId, "Success! Welcome to the Dungeon Tungeon!", absSender);
            } else {
                UserInterface.sendTelegramMessage(chatId, "Sorry, you cannot enter the Dungeon :( \nPlease provide the correct password...", absSender);
            }
        }

    }

    abstract class BaseCommand implements IBotCommand {
        AbsSender sender;
        Message message;
        long chatId;
        @Override
        public void processMessage(AbsSender absSender, Message message, String[] arguments) {
            this.message = message;
            sender = absSender;
            chatId = message.getChatId();
            if (whitelist.contains(chatId)) {
                try {
                    doCommand(arguments);
                } catch (Exception e) {
                    e.printStackTrace();
                    reply("Something went wrong \uD83D\uDE12");
                }
            } else {
                reply("Sorry, you aren't allowed here \uD83D\uDE25\nPlease use /start [password] to begin.");
            }
        }

        protected void reply(String text) {
            UserInterface.sendTelegramMessage(chatId, text, sender);
        }

        abstract protected void doCommand(String[] arguments);

    }

    abstract class GameCommand extends BaseCommand {

        protected World world;
        @Override
        protected void doCommand(String[] arguments) {
            if (worlds.containsKey(chatId)) {
                world = worlds.get(chatId);
                doGameCommand(arguments);
            } else {
                reply("Bros, create the world first!!\n" +
                        "Use /new to start a new world or /load if you already have one");
            }
        }

        protected void worldReply() {
            reply(world.getUi().flush());
        }

        abstract protected void doGameCommand(String[] args);

    }

    class GoCommand extends GameCommand{
        private final int choice;
        GoCommand(int choice) {
            this.choice = choice;
        }
        @Override
        protected void doGameCommand(String[] args) {
            if(!world.move(choice)) {
                reply("\uD83E\uDD21 Invalid move command. Please choose a path that exists.");
            }
            world.printState();
            worldReply();
        }

        @Override
        public String getCommandIdentifier() {
            return "go" + choice;
        }

        @Override
        public String getDescription() {
            return "Go to path #" + choice;
        }
    }

    class InventoryCommand extends GameCommand{
        @Override
        protected void doGameCommand(String[] args) {
            world.getPlayer().printInventory();
            worldReply();
        }

        @Override
        public String getCommandIdentifier() {
            return "inv";
        }

        @Override
        public String getDescription() {
            return "See your current inventory.";
        }
    }

    class StatusCommand extends GameCommand{
        @Override
        protected void doGameCommand(String[] args) {
            world.getPlayer().printState();
            world.getPlayer().printInventory();
            worldReply();
        }

        @Override
        public String getCommandIdentifier() {
            return "stat";
        }

        @Override
        public String getDescription() {
            return "See your status like health and xp";
        }
    }

    class AttackCommand extends GameCommand{
        @Override
        protected void doGameCommand(String[] args) {
            int choice = 0;
            if (args.length != 0) {
                choice = Integer.parseInt(args[0])-1;
            }
            if (world.attack(choice)) {
                world.printState();
            }

            worldReply();
        }

        @Override
        public String getCommandIdentifier() {
            return "attack";
        }

        @Override
        public String getDescription() {
            return "attack an enemy of choice or #1";
        }
    }

    class NewGameCommand extends BaseCommand {
        @Override
        protected void doCommand(String[] arguments) {
            reply("Creating new game \uD83D\uDD03");
            World world = new WorldFactory().generate();
            world.setUi(new UITelegramOutput());
            worlds.put(chatId, world);
            new WorldFactory().save(world, "chat_" + chatId + ".json");
            world.getUi().flush();
            world.printState();
            reply(world.getUi().flush());
        }

        @Override
        public String getCommandIdentifier() {
            return "new";
        }

        @Override
        public String getDescription() {
            return "Create a new world.";
        }
    }

    private void addToWhitelist(long chatID) {
        whitelist.add(chatID);
        JSONObject config = father.getConfig();
        JSONArray wl = new JSONArray(whitelist);
        config.put("whitelist", wl);
        father.saveConfig();
    }

    private void loadWhitelist(JSONObject config) {
        JSONArray wl = config.optJSONArray("whitelist", new JSONArray());
        whitelist.clear();
        for (int i = 0; i < wl.length(); i++) {
            whitelist.add(wl.getLong(i));
        }
    }



}
