package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.storage.Storage;
import com.skyteeee.tungeon.utils.AwaitingAttackCommand;
import com.skyteeee.tungeon.utils.UserInterface;
import org.json.JSONObject;

public class Player extends EntityClass implements Character {
    private int currentPlaceId;
    private int currentArmor;
    private final Inventory inventory;
    private static final int INITIAL_HEALTH = 250;
    private int baseHealth = INITIAL_HEALTH;
    private int health = INITIAL_HEALTH;
    private int level = 1;
    private int xp = 0;
    private int turnsSinceDamaged = 0;
    private String title = "Player 1";

    public Player(World world) {
        setWorld(world);
        inventory = new Inventory(world);
    }

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
        return (Armor)getWorld().getStorage().getItem(currentArmor);
    }

    public boolean equipArmor(int invIdx) {
        if (inventory.getItem(invIdx) instanceof Armor armor) {
            inventory.removeItem(armor);
            if (currentArmor != 0) {
                inventory.addItem(getArmor());
                getWorld().getUi().slowPrint("You took off " + getArmor().getTitle() + "\n");
            }
            setArmor(armor);
            getWorld().getUi().slowPrint("You equipped " + armor.getTitle() + "\n");
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

    public int getTurnsSinceDamaged() {
        return turnsSinceDamaged;
    }

    public void setTurnsSinceDamaged(int turns) {
        turnsSinceDamaged = turns;
    }

    @Override
    public void take(int choice) {
        Item item = getCurrentPlace().give(choice);
        Item popped = inventory.addItem(item);
        if (popped != null) {
            getCurrentPlace().take(popped);
        }
        getWorld().getUi().println("You took a " + item.getTitle());
    }

    @Override
    public Item give(int choice) {
        Item item = inventory.getItem(choice);
        inventory.removeItem(item);
        getWorld().getUi().println("You dropped a " + item.getTitle());
        return item;
    }

    public void onTurn() {
        turnsSinceDamaged++;
        if (turnsSinceDamaged > 3) {
            selfHeal();
        }
    }

    private void selfHeal() {
        int amountToHeal = baseHealth - getHealth();
        if (amountToHeal > 0) {
            int healthToAdd = baseHealth/20 + amountToHeal/10;
            setHealth(Math.min(health + healthToAdd, baseHealth));
        }
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
            getWorld().getUi().strike();
            getWorld().getUi().slowPrint("LEVEL UP!!! YOU ARE NOW LEVEL " + ++level + "\n");
            getWorld().getUi().strike();
            world.onLevelUp(level);
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void printInventory() {
        getWorld().getUi().strike();
        getWorld().getUi().println("You have the following items: ");
        inventory.printState(true);
        if (currentArmor != 0) {
            Armor armor = getArmor();
            getWorld().getUi().slowPrint("You are wearing " + armor.getTitle());
            getWorld().getUi().slowPrint(" | Absorption: " + armor.getAbsorption(true) + "; Defence: " + armor.getDefence() + "\n");
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

    public boolean attack(int enemyIdx) {
        Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
        if (enemy == null) {
            getWorld().getUi().println("\uD83E\uDD21 Enemy selected is not valid. Please select an enemy that exists.");
            return false;
        }
        if (inventory.isEmpty() || noWeapons()) {
            getWorld().getUi().println("As you leap towards the enemy, you realize that you lack any weapons. It is too late to turn away now. You attack it with your bare hands");
            attack(enemy, Weapon.BARE_HANDS);
            return true;
        } else {
            getWorld().getUi().println("You have the following items: ");
            inventory.printState(true);
            getWorld().getUi().println("Which weapon would you like to use? ");
            getWorld().setAwaitingCommand(new AwaitingAttackCommand(enemyIdx));
        }
        return false;
    }

    public boolean attack(int enemyIdx, int weaponIdx) {
        try {
            Item selected = inventory.getItem(weaponIdx);
            if (selected instanceof Weapon weapon) {
                Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
                attack(enemy, weapon);
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            getWorld().getUi().println("\uD83E\uDD21 Selected value not in inventory. Please select a weapon that exists.");
            printInventory();
        }
        return false;
    }

    public void attack(int enemyIdx, UserInterface ui) {
        Enemy enemy = getCurrentPlace().getEnemy(enemyIdx);
        if (inventory.isEmpty() || noWeapons()) {
            getWorld().getUi().println("As you leap towards the enemy, you realize that you lack any weapons. It is too late to turn away now. You attack it with your bare hands");
            attack(enemy, Weapon.BARE_HANDS);
        } else {
            getWorld().getUi().println("You have the following items: ");
            inventory.printState(true);
            Item selected = inventory.getItem(ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1);
            while (!(selected instanceof Weapon weapon)) {
                getWorld().getUi().slowPrint("Selected item is not a weapon. Please select a weapon. \n");
                selected = inventory.getItem(ui.inputChoice("Which weapon would you like to use? ", inventory.size()) - 1);
            }
            attack(enemy, weapon);
        }
    }

    @Override
    public void attack(Character target, Weapon weapon) {
        getWorld().getUi().slowPrint("Attacking " + target.getTitle() + "\n");
        target.defend(this, weapon);
        if (weapon.getDurability() <= 0) {
            getWorld().getUi().slowPrint("Unfortunately, you have lost your faithful " + weapon.getTitle() + ". It broke after delivering its final blow.");
            getWorld().getStorage().removeEntity(weapon);
            inventory.removeItem(weapon);
        }
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
                getWorld().getUi().slowPrint("You lost your " + armor.getShortTitle() + ". It broke after you were attacked.");
                currentArmor = 0;
                getWorld().getStorage().removeEntity(armor);
            }

        }
        damage = Math.max(damage, 5);
        health -= damage;
        setTurnsSinceDamaged(0);
        weapon.applyDamage(weapon.getDamage());
        if (!checkDeath()) {
            getWorld().getUi().slowPrint("You stood your ground and survived the vicious attack. \n" +
                    "You have " + getHealth() + " health remaining.\n");
        }
    }

    private boolean checkDeath() {
        if (health <= 0) {
            getWorld().getUi().slowPrint("Wait", 100);
            for (int i = 0; i < 3; i++) {
                getWorld().getUi().sleep(400);
                getWorld().getUi().print(".");
            }
            getWorld().getUi().println();
            getWorld().getUi().sleep(1000);
            getWorld().getUi().println("Why is it so cold and dark here?");
            getWorld().getUi().sleep(1000);
            getWorld().getUi().println("YOU DIED. HOW PITIFUL...");
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
        getCurrentPlace().removePlayer(this);
        setCurrentPlace(place);
        place.addPlayer(this);
    }

    @Override
    public Place getCurrentPlace() {
        return (Place) getWorld().getStorage().getEntity(currentPlaceId);
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
        baseHealth = Math.max(health, getHealth()) + (int) (health * level * 0.1);
        setHealth(baseHealth);
    }

    public void printState() {
        getWorld().getUi().slowPrint(getTitle() + " | health : " + health + " | xp: " + getXP() + "/" + getLevelThreshold() + " | level: " + getLevel() + "\n");
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
        object.put("turnsSinceDamaged", getTurnsSinceDamaged());
        object.put("baseHealth", baseHealth);
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
        setTurnsSinceDamaged(object.optInt("turnsSinceDamaged", 0));
        baseHealth = object.optInt("baseHealth", INITIAL_HEALTH);
    }
}
