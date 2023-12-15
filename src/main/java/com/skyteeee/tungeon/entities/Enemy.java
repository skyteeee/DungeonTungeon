package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

public class Enemy extends EntityClass implements Character{
    private int currentPlaceId;
    private Inventory inventory = new Inventory();
    private int currentArmor = 0;
    private String title;
    private int weaponIdx = 0;
    private int level = 1;

    private static final int INITIAL_HEALTH = 100;
    private int health = INITIAL_HEALTH;

    public Enemy(int level) {
        this.level = level;
        health = INITIAL_HEALTH + (int) (INITIAL_HEALTH * (level-1) * 0.1);
    }

    @Override
    public void setCurrentPlace(int id) {

        currentPlaceId = id;
    }

    @Override
    public void setCurrentPlace(Place place) {
        place.addEnemy(this);
        currentPlaceId = place.getId();
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) Storage.getInstance().getEntity(currentPlaceId);
    }

    public Armor getArmor() {
        if (currentArmor == 0) return null;
        return (Armor)Storage.getInstance().getItem(currentArmor);
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
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void take(int choice) {

    }

    @Override
    public Item give(int choice) {
        return null;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void printState() {
        System.out.println(title + " holding a " + inventory.getItem(weaponIdx).getTitle());
    }

    private int xpOnDeath(Character killer) {
        int xp = 100 * level + (level-killer.getLevel()) * 10;
        return Math.max(xp, 3);
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
        UserInterface.slowPrint("Dealt " + damage + " damage. ");
        if (!checkDeath()) {
            UserInterface.slowPrint(getTitle() + " has survived your attack. It has " + health + " health remaining. \n");
            attack(attacker, (Weapon) inventory.getItem(weaponIdx));
        } else {
            if (attacker instanceof Player player) {
                player.addXP(xpOnDeath(attacker));
            }
        }
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        if (weapon == null) {
            weapon = (Weapon) inventory.getItem(weaponIdx);
        }
        UserInterface.slowPrint(getTitle() + " attacks " + target.getTitle() + " with " + weapon.getTitle() + ". \n");
        target.defend(this, weapon);
    }

    private boolean checkDeath() {
        if (health <= 0) {
            System.out.println("KILLED: " + title);
            inventory.dropAll(getCurrentPlace());
            Place place = getCurrentPlace();
            getArmor().drop(place);
            place.removeEnemy(this);
            return true;
        }
        return false;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("id", getId());
        object.put("title", getTitle());
        object.put("inventory", inventory.serialize());
        object.put("currentPlace", currentPlaceId);
        object.put("health", getHealth());
        object.put("level", getLevel());
        object.put("armor", getArmorId());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        setId(object.getInt("id"));
        setTitle(object.getString("title"));
        setCurrentPlace(object.getInt("currentPlace"));
        setArmor(object.optInt("armor", 0));
        inventory.deserialize(object.getJSONObject("inventory"));
        setHealth(object.optInt("health", getHealth()));
        setLevel(object.optInt("level", 1));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
