package com.skyteeee.tungeon;

import com.skyteeee.tungeon.entities.*;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Sellable;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.*;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.List;

public class World implements GameObject, Savable {
    private Player player;
    private int spawnId;
    private final EntityFactory factory;
    private final Storage storage;
    private UIOutput ui = new UIOutput();
    private int totalPlaces = 10;
    private AwaitingCommand awaitingCommand = null;

    public World () {
        this.storage = new Storage();
        this.factory = new EntityFactory(this);

    }

    public void setAwaitingCommand(AwaitingCommand command) {
        awaitingCommand = command;
    }

    public AwaitingCommand getAwaitingCommand() {
        return awaitingCommand;
    }

    public UIOutput getUi() {
        return ui;
    }
    public void setUi(UIOutput ui) {
        this.ui = ui;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setTotalPlaces(int places) {
        totalPlaces = places;
    }

    public Player getPlayer() {
        return player;
    }
    public void setSpawn(int id) {
        spawnId = id;
    }

    public void setSpawn(Place place) {
        setSpawn(place.getId());
    }

    public Place getSpawn() {
        return storage.getPlace(spawnId);
    }

    public EntityFactory getFactory() {
        return factory;
    }

    public void printState() {
        ui.println();
        ui.strike();
        player.getCurrentPlace().printState(player);
    }

    private void nextTurn() {
        Storage instance = storage;
        awaitingCommand = null;
        List<Turnable> turnables = instance.getAllOfType(Turnable.class);
        for (Turnable turnable: turnables) {
            if (instance.getEntity(turnable.getId()) != null) {
                turnable.onTurn();
            }
        }
        instance.nextTurn();
    }

    public void buy(int choice) {
        Merchant merchant = player.getCurrentPlace().getMerchant();
        Sellable item = (Sellable) merchant.give(choice);
        if (item != null) player.buy(item,merchant.getSkill());
        else ui.println("Invalid choice!");
    }

    public void give(int choice) {
        player.take(choice);
    }

    public void take(int choice) {
        Item item = player.give(choice);
        player.getCurrentPlace().take(item);
    }

    public void give() {
        Inventory inventory = player.getCurrentPlace().getInventory();
        ui.println("Please choose an item to take: ");
        inventory.printState(true);
        setAwaitingCommand(new AwaitingTakeCommand());
    }


    public void attack(int enemyIdx, UserInterface ui) {
        player.attack(enemyIdx, ui);
        nextTurn();
    }

    public boolean attack(int enemyIdx) {
        if (player.attack(enemyIdx)) {
            nextTurn();
            return true;
        }
        return false;
    }

    public boolean attack(int enemyIdx, int weaponIdx) {
        if (player.attack(enemyIdx, weaponIdx)) {
            nextTurn();
            return true;
        }
        return false;
    }

    public void onPlayerDeath() {
        player.resurrect(getSpawn());
        storage.resetTurn();
    }

    public boolean move(int choice) {
        Place place = player.getCurrentPlace();
        Path path = place.getPath(choice-1);
        if (path != null) {
            boolean attacked = false;
            for (int i = 0; i < place.getEnemyAmount(); i++) {
                Enemy enemy = place.getEnemy(i);
                if (enemy.willAttack()) {
                    enemy.attack(player, null);
                    attacked = true;
                }
            }
            if (!attacked) {
                Place destination = path.getDestination(place);
                path.addVisitor(player);
                player.setCurrentPlace(destination);
                destination.addPlayer(player);
                place.removePlayer(player);
            }
            nextTurn();
            return true;
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.setWorld(this);
    }

    public void onLevelUp(int level) {
        int enemiesToMake = (int)(totalPlaces * 0.2 + level * 0.2);
        int weaponsToMake = (int)(totalPlaces * 0.2 + level * 0.15);
        int armorsToMake = (int)(totalPlaces * 0.2 + level * 0.1);
        Place exclude = player.getCurrentPlace();

        factory.scatterEnemies((int)(enemiesToMake * 0.15), level-1, exclude);
        factory.scatterEnemies((int)(enemiesToMake * 0.55), level, exclude);
        factory.scatterEnemies((int)(enemiesToMake * 0.30), level+1, exclude);


        factory.scatterWeapons((int)(weaponsToMake * 0.25), level-1, exclude);
        factory.scatterWeapons((int)(weaponsToMake * 0.55), level, exclude);
        factory.scatterWeapons((int)(weaponsToMake * 0.2), level+1, exclude);

        factory.scatterArmor((int)(armorsToMake * 0.3), level-1, exclude);
        factory.scatterArmor((int)(armorsToMake * 0.55), level, exclude);
        factory.scatterArmor((int)(armorsToMake * 0.15), level+1, exclude);
    }


    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("spawn", spawnId);
        object.put("player", player.getId());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setSpawn(object.getInt("spawn"));
        int playerId = object.optInt("player", 0);
        if (playerId != 0) {
            setPlayer((Player) storage.getEntity(playerId));
        }
    }
}
