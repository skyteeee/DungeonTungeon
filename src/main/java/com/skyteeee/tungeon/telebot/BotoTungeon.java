package com.skyteeee.tungeon.telebot;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.utils.*;
import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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

    private void sendMessage(long chatId, String toSend, ReplyKeyboard keyboard) {
        SendMessage out = new SendMessage(String.valueOf(chatId), toSend);
        out.setReplyMarkup(keyboard);
        try {
            execute(out);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        out.log("received non command update");
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String contents = message.getText();
            processAwait(message.getChatId(), contents);
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            String contents = query.getData();
            String[] parts = contents.split(" ");
            if (parts.length > 1) {
                processQueryCommand(query.getMessage().getChatId(), parts);
            } else {
                processAwait(query.getMessage().getChatId(), contents);
            }
            try {
                execute(new AnswerCallbackQuery(query.getId()));
            } catch (Exception e) {

            }
        }

    }
    private ReplyKeyboard mainKeyboard(World world) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow goRow = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        Place place = world.getPlayer().getCurrentPlace();
        for (int i = 0; i < place.getPathCount(); i++) {
            String title = "/go"+(i+1);
            if (place.getPath(i).hasVisited(world.getPlayer())) {
                title += " :round_pushpin:";
            }
            goRow.add(EmojiParser.parseToUnicode(title));

        }

        KeyboardRow attackRow = new KeyboardRow();
        for (int i = 0; i < place.getEnemyAmount(); i++) {
            String title = "/attack " + (i+1) + " :crossed_swords:";
            attackRow.add(EmojiParser.parseToUnicode(title));
        }

        KeyboardRow statusRow = new KeyboardRow();
        statusRow.add(EmojiParser.parseToUnicode("/stat :sparkling_heart:"));
        if (!place.getInventory().isEmpty()) statusRow.add(EmojiParser.parseToUnicode("/take :school_satchel:"));

        // Add the first row to the keyboard
        keyboard.add(goRow);
        if (!attackRow.isEmpty()) keyboard.add(attackRow);
        keyboard.add(statusRow);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return  keyboardMarkup;
    }

    private void processQueryCommand(long chatId, String[] parts) {
        World world = worlds.get(chatId);
        switch (parts[0]) {
            case "drop" :
                world.take(Integer.parseInt(parts[1]) - 1);
                break;
            case "equip" :
                world.getPlayer().equipArmor(Integer.parseInt(parts[1])-1);
                break;
            case "pickAttack" :
                processAwait(chatId, parts[1]);
                return;
        }

        world.printState();
        sendMessage(chatId,world.getUi().flush(), mainKeyboard(world));

    }

    private void processAwait(long chatId, String contents) {
        World world = worlds.get(chatId);
        if (world != null && world.getAwaitingCommand() != null) {
            AwaitingCommand command = world.getAwaitingCommand();
            try {
                command.process(world, Integer.parseInt(contents)-1);
                sendMessage(chatId,world.getUi().flush(), mainKeyboard(world));
            } catch (Exception e) {
                sendMessage(chatId, "\uD83E\uDD21 We wanted a number...");
            }
        }
    }

    private void createMenu(long chatId) {
        ArrayList<BotCommand> commands = new ArrayList<>();
        commands.add(BotCommand.builder().command("new").description("Create a new game.").build());
        commands.add(BotCommand.builder().command("load").description("Load saved game.").build());
        commands.add(BotCommand.builder().command("save").description("Save the current game state.").build());
        SetMyCommands myCommands = SetMyCommands.builder().commands(commands).languageCode("en")
                .scope(BotCommandScopeChat.builder().chatId(chatId).build()).build();
        SetChatMenuButton button = SetChatMenuButton.builder()
                .menuButton(MenuButtonCommands.builder().build()).build();
        button.setChatId(chatId);
        try {
            myCommands.validate();
            button.validate();
            execute(myCommands);
            execute(button);
        } catch (TelegramApiException ignored) {
            ignored.printStackTrace();
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
        register(new TakeCommand());
        register(new LoadCommand());
        register(new SaveCommand());
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
                createMenu(chatId);
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

        protected void reply(String text, ReplyKeyboard keyboard) {
            UserInterface.sendTelegramMessage(chatId, text, sender, keyboard);
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
            reply(world.getUi().flush(), mainKeyboard(world));
        }

        protected void worldReply(ReplyKeyboard keyboard) {
            reply(world.getUi().flush(), keyboard);
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
            worldReply(generateKeyboard());
        }

        private ReplyKeyboard generateKeyboard() {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            Inventory inventory = world.getPlayer().getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(EmojiParser.parseToUnicode(":x: Drop " + (i+1)));
                button.setCallbackData("drop "+(i+1));
                rowInline.add(button);

                if (inventory.getItem(i) instanceof Armor) {
                    InlineKeyboardButton equip = new InlineKeyboardButton();
                    equip.setText(EmojiParser.parseToUnicode(":shield: Equip " + (i+1)));
                    equip.setCallbackData("equip "+(i+1));
                    rowInline.add(equip);
                }


                rowsInline.add(rowInline);
            }
            // Set the keyboard to the markup
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            return markupInline;
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
                worldReply();
            } else {
                worldReply(generateKeyboard());
            }

        }

        private ReplyKeyboard generateKeyboard() {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            Inventory inventory = world.getPlayer().getInventory();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.getItem(i) instanceof Weapon) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(EmojiParser.parseToUnicode(":crossed_swords: " + (i+1)));
                    button.setCallbackData("pickAttack "+(i+1));
                    rowInline.add(button);
                }
            }
            rowsInline.add(rowInline);
            // Set the keyboard to the markup
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            return markupInline;
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

    class TakeCommand extends GameCommand {
        @Override
        protected void doGameCommand(String[] args) {
            Inventory inventory = world.getPlayer().getCurrentPlace().getInventory();
            if (inventory.isEmpty()) {
                reply("Sorry, there is nothing to take here. :frowning:");
                return;
            }
            world.give();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int i = 0; i < inventory.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(EmojiParser.parseToUnicode(":point_right: " + (i+1)));
                button.setCallbackData(String.valueOf(i+1));
                rowInline.add(button);
            }
            // Set the keyboard to the markup
            rowsInline.add(rowInline);
            // Add it to the message
            markupInline.setKeyboard(rowsInline);
            worldReply(markupInline);
        }

        @Override
        public String getCommandIdentifier() {
            return "take";
        }

        @Override
        public String getDescription() {
            return "take an item from the ground";
        }
    }

    class ShopCommand extends GameCommand {
        @Override
        protected void doGameCommand(String[] args) {

        }

        @Override
        public String getCommandIdentifier() {
            return "shop";
        }

        @Override
        public String getDescription() {
            return "exchange treasure for goods";
        }
    }

    class SaveCommand extends GameCommand {
        @Override
        protected void doGameCommand(String[] args) {
            new WorldFactory().save(world, "chat_" + chatId + ".json");
            world.getUi().flush();
            reply(EmojiParser.parseToUnicode(":white_check_mark: Successfully saved!"));
        }

        @Override
        public String getCommandIdentifier() {
            return "save";
        }

        @Override
        public String getDescription() {
            return "Save your current game state.";
        }
    }

    class LoadCommand extends BaseCommand {
        @Override
        protected void doCommand(String[] arguments) {
            World world = new WorldFactory().load("chat_" + chatId + ".json");
            if (world == null) {
                reply(EmojiParser.parseToUnicode(":no_entry: No save file found!"));
                return;
            }
            world.setUi(new UITelegramOutput());
            world.printState();
            reply(world.getUi().flush(), mainKeyboard(world));
            worlds.put(chatId, world);
        }

        @Override
        public String getCommandIdentifier() {
            return "load";
        }

        @Override
        public String getDescription() {
            return "load saved game.";
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
            reply(world.getUi().flush(), mainKeyboard(world));
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
            long chatId = wl.getLong(i);
            whitelist.add(chatId);
        }
    }

    public void afterStart() {
        for (long chatId : whitelist) {
            createMenu(chatId);
        }
    }



}
