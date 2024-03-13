package com.skyteeee.tungeon.telebot;

import com.skyteeee.tungeon.utils.UIOutput;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BotoBatya {

    private static final String CONFIG_FILE_NAME = "BotoConfig.json";
    private BotoTungeon boto;
    private TelegramBotsApi botsApi;
    private JSONObject config;
    private final UIOutput out = new UIOutput();

    public void setup(String password) {
        config = loadConfig();
        if (config != null) {
            String botName = config.getString("name");
            String botToken = config.getString("token");
            boto = new BotoTungeon(botToken, botName, password, this);
            try {
                botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(boto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject loadConfig() {
        java.nio.file.Path loadPath = Paths.get(CONFIG_FILE_NAME);
        String fileContents = null;
        try {
            fileContents = Files.readString(loadPath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            out.println("Could not read Boto config from " + loadPath.toAbsolutePath());
            return null;
        }

        return new JSONObject(fileContents);

    }

    public JSONObject getConfig() {
        return config;
    }

    public void saveConfig() {
        java.nio.file.Path savePath = Paths.get(CONFIG_FILE_NAME);

        try {
            Files.writeString(savePath, config.toString(2), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            // nothing to do
        }
    }

}
