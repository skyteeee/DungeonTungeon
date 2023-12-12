package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.UserInterface;
import com.sun.source.tree.WhileLoopTree;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private int currentArmor;
    private final Inventory inventory = new Inventory();
    private static final int INITIAL_HEALTH = 250;
    private int health = INITIAL_HEALTH;
    private String title = "Player 1";
    @Override
    public void setCurrentPlace(Place place) {
        currentPlaceId = place.getId();
    }
    @Override
    public void setCurrentPlace(int id) {
        currentPlaceId = id;
    }

    public Armor getArmor() {
        if (currentArmor == 0) return null;
        return (Armor)Storage.getInstance().getItem(currentArmor);
    }

    public boolean equipArmor(int invIdx) {
        if (inventory.getItem(invIdx) instanceof Armor armor) {
            if (currentArmor != 0) {
                getCurrentPlace().take(getArmor());
                inventory.removeItem(armor);
            }
            setArmor(armor);
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
        inventory.addItem(item);
        System.out.println("You took a " + item.getTitle());
    }

    @Override
    public Item give(int choice) {
        Item item = inventory.getItem(choice);
        inventory.removeItem(item);
        System.out.println("You dropped a " + item.getTitle());
        return item;
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
            UserInterface.slowPrint(" | Absorption: " + ((int)((1 - armor.getAbsorption()) * 100) / 100f) + "; Defence: " + armor.getDefence() + "\n");
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
        UserInterface.slowPrint(getTitle() + " | health : " + health + "\n");
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("currentPlace", currentPlaceId);
        object.put("inventory", inventory.serialize());
        object.put("health", getHealth());
        object.put("armor", getArmorId());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setCurrentPlace(object.getInt("currentPlace"));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
        setArmor(object.optInt("armor", 0));
    }
}
