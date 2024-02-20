package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.EntityFactory;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

import java.util.List;

public class Enemy extends EntityClass implements Character{
    private int currentPlaceId;
    private Inventory inventory = new Inventory();
    private int currentArmor = 0;
    private String title;
    private int weaponIdx = 0;
    private int level = 1;

    private static final int INITIAL_HEALTH = 100;
    private float attackChance;
    private int health;

    private boolean usedTurn = false;

    public Enemy(int level) {
        this.level = level;
        health = INITIAL_HEALTH;
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

    public void setAttackChance(float chance) {
        attackChance = chance;
    }

    public float getAttackChance() {
        return attackChance;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Weapon getCurrentWeapon() {
        return (Weapon) getInventory().getItem(weaponIdx);
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
        System.out.print(title + " ");
        if (weaponIdx != -1) {
            System.out.print("holding a " + inventory.getItem(weaponIdx).getTitle());
        }
        if (currentArmor != 0) {
            System.out.print("; it is wearing " + getArmor().getShortTitle());
        }
        System.out.println();
    }

    private int xpOnDeath(Character killer) {
        int xp = 100 * level + (level-killer.getLevel()) * 10 + EntityFactory.rnd.nextInt(20)-10;
        return Math.max(xp, 3);
    }

    @Override
    public void defend(Character attacker, Weapon weapon) {
        int damage;
        if (currentArmor == 0) {
            damage = weapon.getDamage();
        } else {
            Armor armor = getArmor();
            armor.applyDamage(weapon.getDamage());
            damage = (int)(weapon.getDamage() * armor.getAbsorption()) - armor.getDefence();
            if (armor.getDurability() <= 0) {
                Storage.getInstance().removeEntity(armor);
                currentArmor = 0;
                UserInterface.slowPrint(title + "'s " + armor.getShortTitle() + " broke.");
            }
        }
        damage = Math.max(damage, 5);
        health -= damage;
        weapon.applyDamage(weapon.getDamage());
        UserInterface.slowPrint("Dealt " + damage + " damage. ");
        if (!checkDeath()) {
            UserInterface.slowPrint(getTitle() + " has survived your attack. It has " + health + " health remaining. \n");
            int currentTurn = Storage.getInstance().getTurn();
            attack(attacker, getCurrentWeapon());
            alertHoard(attacker, currentTurn);
        } else {
            if (attacker instanceof Player player) {
                player.addXP(xpOnDeath(attacker));
            }
        }
    }

    public void callToArms(Character aggressor) {
        if (aggressor.getLevel() < getLevel()
                || (aggressor.getLevel() == getLevel()
                    && EntityFactory.rnd.nextFloat() >= 0.3f)) {
            attack(aggressor, getCurrentWeapon());
        }
    }

    public void alertHoard(Character aggressor, int currentTurn) {
        Place curentPlace = getCurrentPlace();
        for (int i = 0; i < curentPlace.getEnemyAmount(); i++) {
            Enemy dude = curentPlace.getEnemy(i);
            if (dude != this && aggressor.getHealth() > 0 && Storage.getInstance().getTurn() == currentTurn) dude.callToArms(aggressor);
        }
    }

    public void onTurn() {
        if (!usedTurn) {
            Place currentPlace = getCurrentPlace();
            int enemyAmount = currentPlace.getEnemyAmount();
            if (enemyAmount == 1 || enemyAmount >= EntityFactory.rnd.nextInt(2, 4)) {
                moveSomewhere(currentPlace);
            } else {
                System.out.println("LOG: Enemy did not move. There are " + enemyAmount + " enemies at " + currentPlace.getId());
            }
        }
        usedTurn = false;
    }

    private void moveSomewhere(Place currentPlace) {
        var choices = currentPlace.getDestinations();

        Place destination = choices.get(EntityFactory.rnd.nextInt(choices.size()));
        /** enemy hunts player
        for (Place place : choices) {
            if (place.getPlayerAmount() >= 1) {
                destination = place;
                System.out.println("LOG: Enemy moved towards PLAYER!!! Into " + destination.getId());
                break;
            }
        }
         **/
        currentPlace.removeEnemy(this);
        setCurrentPlace(destination);
        System.out.println("LOG: Enemy " + getTitle() + " moved from " + currentPlace.getId() + " to " + destination.getId() +
                ". There are now " + destination.getEnemyAmount() + " enemies there.");
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        if (weapon == null) {
            if (weaponIdx != -1) {
                weapon = getCurrentWeapon();
            } else {
                weapon = Weapon.BARE_HANDS;
            }
        }
        UserInterface.slowPrint(getTitle() + " attacks " + target.getTitle() + " with " + weapon.getTitle() + ". \n");
        usedTurn = true;
        target.defend(this, weapon);
        if (weaponIdx != -1 && weapon.getDurability() <= 0) {
            inventory.removeItem(weapon);
            weaponIdx = -1;
            System.out.println(title + "'s " + weapon.getTitle() + " broke.");
        }
    }

    public boolean willAttack() {
        return EntityFactory.rnd.nextFloat() < attackChance;
    }

    private boolean checkDeath() {
        if (health <= 0) {
            System.out.println("KILLED: " + title);
            inventory.dropAll(getCurrentPlace());
            Place place = getCurrentPlace();
            getArmor().drop(place);
            place.removeEnemy(this);
            Storage.getInstance().removeEntity(this);
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
        object.put("attackChance", getAttackChance());
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
        setAttackChance(object.optFloat("attackChance", 0f));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
