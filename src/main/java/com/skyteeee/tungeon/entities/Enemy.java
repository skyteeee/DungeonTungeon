package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;
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
    private Inventory inventory;
    private int currentArmor = 0;
    private String title;
    private int weaponIdx = 0;
    private int level = 1;
    private float mergeChance = 0f;

    private static final int INITIAL_HEALTH = 100;
    private float attackChance;
    private int health;

    private boolean usedTurn = false;

    public Enemy(int level, World world) {
        this.level = level;
        this.setWorld(world);
        inventory = new Inventory(world);
        health = INITIAL_HEALTH;
    }

    public float getMergeChance() {
        return mergeChance;
    }

    public void setMergeChance(float chance) {
        mergeChance = chance;
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
        return (Place) getWorld().getStorage().getEntity(currentPlaceId);
    }

    public Armor getArmor() {
        if (currentArmor == 0) return null;
        return (Armor)getWorld().getStorage().getItem(currentArmor);
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
        getWorld().getUi().print(title + " ");
        if (weaponIdx != -1) {
            getWorld().getUi().print("holding a " + inventory.getItem(weaponIdx).getTitle());
        }
        if (currentArmor != 0) {
            getWorld().getUi().print("; it is wearing " + getArmor().getShortTitle());
        }
        getWorld().getUi().println();
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
                getWorld().getStorage().removeEntity(armor);
                currentArmor = 0;
                getWorld().getUi().slowPrint(title + "'s " + armor.getShortTitle() + " broke.");
            }
        }
        damage = Math.max(damage, 5);
        health -= damage;
        weapon.applyDamage(weapon.getDamage());
        getWorld().getUi().slowPrint("Dealt " + damage + " damage. ");
        if (!checkDeath()) {
            getWorld().getUi().slowPrint(getTitle() + " has survived your attack. It has " + health + " health remaining. \n");
            int currentTurn = getWorld().getStorage().getTurn();
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
            if (dude != this && aggressor.getHealth() > 0 && getWorld().getStorage().getTurn() == currentTurn) dude.callToArms(aggressor);
        }
    }

    public void onTurn() {
        if (!usedTurn) {
            Place currentPlace = getCurrentPlace();
            int enemyAmount = currentPlace.getEnemyAmount();
            if (enemyAmount == 1 || enemyAmount >= EntityFactory.rnd.nextInt(2, 4)) {
                moveSomewhere(currentPlace);
            } else {
                getWorld().getUi().println("LOG: Enemy did not move. There are " + enemyAmount + " enemies at " + currentPlace.getId());
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
                getWorld().getUi().println("LOG: Enemy moved towards PLAYER!!! Into " + destination.getId());
                break;
            }
        }
         **/
        currentPlace.removeEnemy(this);
        setCurrentPlace(destination);

        //MERGE
        if (EntityFactory.rnd.nextFloat() < mergeChance && destination.getEnemyAmount() > 1
                && destination.getPlayerAmount() == 0) {
            int amount = destination.getEnemyAmount();
            getWorld().getUi().println("[LOG] attempting merge at " + currentPlaceId);
            for (int i = 0; i < amount; i++) {
                Enemy e = destination.getEnemy(i);
                if (e.getId() != getId()) {
                    currentPlace.getWorld().getFactory().mergeEnemies(this, e);
                    break;
                }
            }
        }
        //getWorld().getUi().println("LOG: Enemy " + getTitle() + " moved from " + currentPlace.getId() + " to " + destination.getId() +
        //        ". There are now " + destination.getEnemyAmount() + " enemies there.");
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
        getWorld().getUi().slowPrint(getTitle() + " attacks " + target.getTitle() + " with " + weapon.getTitle() + ". \n");
        usedTurn = true;
        target.defend(this, weapon);
        if (weaponIdx != -1 && weapon.getDurability() <= 0) {
            inventory.removeItem(weapon);
            getWorld().getStorage().removeEntity(weapon);
            weaponIdx = -1;
            getWorld().getUi().println(title + "'s " + weapon.getTitle() + " broke.");
        }
    }

    public boolean willAttack() {
        return EntityFactory.rnd.nextFloat() < attackChance;
    }

    private boolean checkDeath() {
        if (health <= 0) {
            getWorld().getUi().println("KILLED: " + title);
            inventory.dropAll(getCurrentPlace());
            Place place = getCurrentPlace();
            getArmor().drop(place);
            place.removeEnemy(this);
            getWorld().getStorage().removeEntity(this);
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
        object.put("mergeChance", getMergeChance());
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
        setMergeChance(object.optFloat("mergeChance", 0f));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
