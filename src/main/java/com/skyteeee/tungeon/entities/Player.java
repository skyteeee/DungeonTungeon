package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.UserInterface;
import com.sun.source.tree.WhileLoopTree;
import org.json.JSONObject;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private int currentArmor;
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
    public Armor getArmor() {
        if (currentArmor == 0) return null;
        return (Armor)Storage.getInstance().getItem(currentArmor);
    }

    public boolean equipArmor(int invIdx) {
        if (inventory.getItem(invIdx) instanceof Armor armor) {
            if (currentArmor != 0) {
                getCurrentPlace().take(getArmor());
                UserInterface.slowPrint("You dropped " + getArmor().getTitle() + "\n");
                inventory.removeItem(armor);
            }
            setArmor(armor);
            UserInterface.slowPrint("You equipped " + armor.getTitle() + "\n");
            return true;
        }
        return false;
    }

    public int getArmorId() {
        return currentArmor;
    }

    public void setArmor(Armor armor) {
        currentArmor = armor.getId();
    }

    public void setArmor(int id) {
        currentArmor = id;
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
            setHealth(INITIAL_HEALTH, level);
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
        if (currentArmor != 0) {
            Armor armor = getArmor();
            UserInterface.slowPrint("You are wearing " + armor.getTitle());
            UserInterface.slowPrint(" | Absorption: " + armor.getAbsorption(true) + "; Defence: " + armor.getDefence() + "\n");
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    private boolean noWeapons() {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getItem(i) instanceof Weapon) {
                return false;
            }
        }
        return true;
    }

    public void attack(int enemyIdx, UserInterface ui) {
        Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
        if (inventory.isEmpty() || noWeapons()) {
            System.out.println("As you leap towards the enemy, you realize that you lack any weapons. It is too late to turn away now. ");
            enemy.attack(this, null);
        } else {
            System.out.println("You have the following items: ");
            inventory.printState(true);
            Item selected = inventory.getItem(ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1);
            while (!(selected instanceof Weapon weapon)) {
                UserInterface.slowPrint("Selected item is not a weapon. Please select a weapon. \n");
                selected = inventory.getItem(ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1);
            }
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
        int damage;
        if (currentArmor == 0) {
            damage = weapon.getDamage();
        } else {
            Armor armor = getArmor();
            damage = (int)(weapon.getDamage() * armor.getAbsorption()) - armor.getDefence();
        }
        damage = Math.max(damage, 0);
        health -= damage;
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
            Place place = getCurrentPlace();
            inventory.dropAll(place);
            getArmor().drop(place);
            setArmor(0);
            world.onPlayerDeath();
            return true;
        }
        return false;
    }

    public void resurrect(Place place) {
        setXP(0);
        setHealth(INITIAL_HEALTH, level);
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

    public void setHealth(int health, int level) {
        setHealth(Math.max(health, getHealth()) + (int) (health * level * 0.1));
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
        object.put("armor", getArmorId());
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
        setArmor(object.optInt("armor", 0));
    }
}
