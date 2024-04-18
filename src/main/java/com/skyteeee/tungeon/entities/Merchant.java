package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.World;
import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Inventory;
import org.json.JSONObject;

public class Merchant extends CharacterClass{

    public Merchant(World world) {
        setWorld(world);
        inventory = new Inventory(world);
    }

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
        return null;
    }

    @Override
    public void attack(Character target, Weapon weapon) {

    }

    @Override
    public void defend(Character attacker, Weapon weapon) {

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
