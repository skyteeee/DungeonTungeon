package com.skyteeee.tungeon.entities;
import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.items.*;
import com.skyteeee.tungeon.storage.Inventory;
import com.skyteeee.tungeon.utils.EntityFactory;
import org.json.JSONObject;

public class Merchant extends CharacterClass implements Turnable{

    public Merchant(World world) {
        setWorld(world);
        shopCycle = EntityFactory.rnd.nextInt(3,10);
        inventory = new Inventory(world);
    }

    int shopCycle;

    public enum Skill {
        REPAIR, ARMOR, WEAPON
    }

    private Skill skill = Skill.ARMOR;

    public void setSkill(Skill s) {
        skill = s;
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public void take(int choice) {

    }

    @Override
    public Item give(int choice) {
        if (choice < inventory.size()) {
            Item item = inventory.getItem(choice);
            inventory.removeItem(item);
            return item;
        }
        return null;
    }

    @Override
    public void attack(Character target, Weapon weapon) {

    }

    @Override
    public void defend(Character attacker, Weapon weapon) {

    }

    @Override
    public void onTurn() {
        if (world.getStorage().getTurn() % shopCycle == 0) {
            clearShop();
        }
    }

    private void clearShop() {
        inventory.clearAll(true);
    }

    public void printState() {
        world.getUi().println("As you talk to " + getTitle() + ", they offer you this: ");
        if (inventory.isEmpty()) {
            populateShop();
        }
        inventory.printState(false);

    }

    private void populateShop() {
        EntityFactory factory = new EntityFactory(world);
        Player player = world.getPlayer();
        int lvl = player.getLevel();
        switch (skill) {
            case ARMOR:
                for (int i = 0; i < 3; i++) {
                    Armor arm = factory.createArmor(lvl);
                    Sellable sellable = factory.createSellable(arm);
                    int price = Math.max(2,(int)(arm.getDefence()/arm.getAbsorption())/(3 * lvl));
                    sellable.setPrice(price);
                    sellable.setTitle(arm.getTitle(true) + " | Def: " + arm.getDefence() + " Abs: " + arm.getAbsorption(true) + " | " + price + " :rotating_light:");
                    inventory.addItem(sellable);
                }
                break;
            case WEAPON:
                for (int i = 0; i < 3; i++) {
                    Weapon wp = factory.createWeapon(lvl);
                    Sellable sellable = factory.createSellable(wp);
                    int price = Math.max(2,(wp.getDamage()/9)/(lvl));
                    sellable.setPrice(price);
                    sellable.setTitle(wp.getTitle(true) + " | Damage: " + wp.getDamage() + " | " + price + " :gem:");
                    inventory.addItem(sellable);
                }
                break;
            case REPAIR:
                Inventory playerInventory = player.getInventory();
                for (int i = 0; i < playerInventory.size(); i++) {
                    if (playerInventory.getItem(i) instanceof Weapon weapon) {
                        Sellable sellable = factory.createSellable(weapon);
                        float fullDurability = 666f + 3 * lvl;
                        sellable.setProperty("durability", String.valueOf(fullDurability));
                        int price =  Math.max(2,(int)(fullDurability - weapon.getDurability())/(3 * lvl));
                        sellable.setPrice(price);
                        sellable.setTitle(weapon.getTitle() + " restored to durability: " + fullDurability + " | " + price + " :money_face:");
                        inventory.addItem(sellable);
                    }

                    if (playerInventory.getItem(i) instanceof Armor armor) {
                        Sellable sellable = factory.createSellable(armor);
                        float fullDurability = 400f + (400 * 0.13f * lvl);
                        sellable.setProperty("durability", String.valueOf(fullDurability));
                        int price =  Math.max(2,(int)(fullDurability - armor.getDurability())/(2 * lvl));
                        sellable.setPrice(price);
                        sellable.setTitle(armor.getTitle() + " restored to durability: " + fullDurability + " | " + price + " :money_face:");
                        inventory.addItem(sellable);
                    }

                }

                Armor armor = player.getArmor();
                if (armor != null) {
                    Sellable sellable = factory.createSellable(armor);
                    float fullDurability = 400f + (400 * 0.13f * lvl);
                    sellable.setProperty("durability", String.valueOf(fullDurability));
                    int price =  Math.max(2,(int)(fullDurability - armor.getDurability())/(2 * lvl));
                    sellable.setPrice(price);
                    sellable.setTitle(armor.getTitle() + " restored to durability: " + fullDurability + " | " + price + " :money_face:");
                    inventory.addItem(sellable);
                }
                break;

        }
    }

    @Override
    public void setCurrentPlace(Place place) {
        super.setCurrentPlace(place);
        place.setMerchant(this);
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = super.serialize();
        object.put("skill", getSkill().toString());
        return object;
    }

    @Override
    public void deserialize(JSONObject object) {
        super.deserialize(object);
        setSkill(Skill.valueOf(object.optString("skill", Skill.REPAIR.toString())));
    }
}
