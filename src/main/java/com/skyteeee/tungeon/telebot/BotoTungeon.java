package com.skyteeee.tungeon.telebot;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.utils.UIOutput;
import com.skyteeee.tungeon.utils.UserInterface;
import com.skyteeee.tungeon.utils.WorldFactory;
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

    @Override
    public void processNonCommandUpdate(Update update) {

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

    class NewGameCommand extends BaseCommand {
        @Override
        protected void doCommand(String[] arguments) {
            reply("Creating new game \uD83D\uDD03");
            World world = new WorldFactory().generate();
            worlds.put(chatId, world);
            new WorldFactory().save(world, "chat_" + chatId + ".json");

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
