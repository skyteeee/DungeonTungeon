package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private final Inventory inventory = new Inventory();
    private static final int INITIAL_HEALTH = 250;
    private int health = INITIAL_HEALTH;
    private int level = 1;
    private int xp = 0;
    private World world;
    private String title = "Player 1";
    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }
    @Override
    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void take(int choice) {
        Item item = getCurrentPlace().give(choice);
        Item popped = inventory.addItem(item);
        if (popped != null) {
            getCurrentPlace().take(popped);
        }
        System.out.println("You took a " + item.getTitle());
    }

    @Override
    public Item give(int choice) {
        Item item = inventory.getItem(choice);
        inventory.removeItem(item);
        System.out.println("You dropped a " + item.getTitle());
        return item;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public void addXP(int xp) {
        this.xp += xp;
        levelUp();
    }

    public int getXP() {
        return xp;
    }

    private int getLevelThreshold() {
        return (int) (100 * Math.pow(Math.E, level));
    }

    private void levelUp() {
        while (getXP() >= getLevelThreshold()) {
            health += (int) (INITIAL_HEALTH * level * 0.1);
            UserInterface.strike();
            UserInterface.slowPrint("LEVEL UP!!! YOU ARE NOW LEVEL " + ++level + "\n");
            UserInterface.strike();
            world.onLevelUp(level);
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void printInventory() {
        UserInterface.strike();
        System.out.println("You have the following items: ");
        inventory.printState(true);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void attack(int enemyIdx, UserInterface ui) {
        Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
        if (inventory.isEmpty()) {
            System.out.println("As you leap towards the enemy, you realize that you lack any weapons. It is too late to turn away now. ");
            enemy.attack(this, null);
        } else {
            System.out.println("You have the following items: ");
            inventory.printState(true);
            int weaponIdx = ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1;
            Weapon weapon = (Weapon) inventory.getItem(weaponIdx);
            attack(enemy, weapon);
        }
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        UserInterface.slowPrint("Attacking " + target.getTitle() + "\n");
        target.defend(this, weapon);
    }

    @Override
    public void defend(Character attacker, Weapon weapon) {
        health -= weapon.getDamage();
        if (!checkDeath()) {
            UserInterface.slowPrint("You stood your ground and survived the vicious attack. \n" +
                    "You have " + getHealth() + " health remaining.\n");
        }
    }

    private boolean checkDeath() {
        if (health <= 0) {
            UserInterface.slowPrint("Wait", 100);
            for (int i = 0; i < 3; i++) {
                UserInterface.sleep(400);
                System.out.print(".");
            }
            System.out.println();
            UserInterface.sleep(1000);
            System.out.println("Why is it so cold and dark here?");
            UserInterface.sleep(1000);
            System.out.println("YOU DIED. HOW PITIFUL...");
            inventory.dropAll(getCurrentPlace());

            return true;
        }
        return false;
    }

    public void resurrect(Place place) {
        setHealth(INITIAL_HEALTH);
        setCurrentPlace(place);
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) Storage.getInstance().getEntity(currentPlaceId);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    public void printState() {
        UserInterface.slowPrint(getTitle() + " | health : " + health + " | xp: " + getXP() + "/" + getLevelThreshold() + " | level: " + getLevel() + "\n");
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("currentPlace", currentPlaceId);
        object.put("inventory", inventory.serialize());
        object.put("health", getHealth());
        object.put("level", getLevel());
        object.put("xp", getXP());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
        setLevel(object.optInt("level", 1));
        setXP(object.optInt("xp", 0));
    }
}
