package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.entities.Character;
import com.skyteeee.tungeon.entities.Outdoors;
import com.skyteeee.tungeon.entities.Path;
import com.skyteeee.tungeon.entities.Place;
import com.skyteeee.tungeon.entities.Player;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Storage;

import javax.swing.text.PlainView;
import java.util.Random;

public class EntityFactory {
    public Random rnd = new Random();
    static String[] colors = new String[] {
            "black",
            "white",
            "green",
            "red",
            "blue",
            "yellow",
            "orange",
            "purple",
            "magenta",
            "cyan",
            "brown",
            "teal",
            "pink",
            "gray",
            "tan"
    };

    static String[] terrains = new String[] {
            "forest",
            "plain",
            "mountains",
            "lake",
            "river",
            "canyon",
            "valley",
            "hills",
            "desert",
            "tundra",
            "glacier",
            "mesa",
            "jungle",
            "oak forest",
            "birch forest",
            "pine forest",
            "beach",
            "ocean"
    };

    static String[] sizes = new String[] {
            "big",
            "large",
            "huge",
            "small",
            "tiny",
            "gigantic",
            "little",
            "regular",
            "narrow",
            "wide",
            "long",
            "short",
            "enormous",
            "infinite",
            "claustrophobic"
    };

    static String[] descriptors = new String[] {
            "wet",
            "misty",
            "foggy",
            "sunny",
            "cloudy",
            "unsettling",
            "eerie",
            "hazy",
            "hot",
            "freezing",
            "dark",
            "light",
            "energetic",
            "hopeful",
            "encouraging",
            "dreamy",
            "luscious",
            "heavenly",
            "juicy",
            "powerful",
            "underwhelming",
            "overwhelming",
            "funny"
    };

    static String[] pathTypes = new String[] {
            "road",
            "paved road",
            "unpaved road",
            "path",
            "way",
            "walkway",
            "parkway",
            "highway",
            "freeway",
            "narrow windy path of the ancients",
            "roundabout"
    };

    static String[] weaponTypes = new String[] {
            "sword",
            "bow",
            "long bow",
            "broadsword",
            "knife",
            "katana",
            "staff",
            "wine bottle",
            "wand",
            "stick",
            "hammer",
            "bat",
            "chain",
            "chainsaw",
            "chopsticks",
            "pencil",
            "guitar",
            "razor blade",
            "shotgun",
            "banana"
    };

    static class WeaponAbility {
        String description;
        static final int MAX_DAMAGE = 100000;

        int[] damageRange = new int[2];
        float dropChance;

        WeaponAbility(String desc, int dmgMin, int dmgMax, float dropChance) {
            description = desc;
            damageRange[0] = dmgMin;
            damageRange[1] = dmgMax;
            this.dropChance = dropChance;

        }

        int getDamage(Random rnd) {
            return rnd.nextInt(damageRange[0], damageRange[1]+1);
        }

    }

    static WeaponAbility[] weaponAbilities = new WeaponAbility[] {
            new WeaponAbility("weak", 1, 10, 0.2f),
            new WeaponAbility("mysterious", 5, 1000, 0.6f),
            new WeaponAbility("strong", 25, 100, 0.4f),
            new WeaponAbility("great", 100, 200, 0.5f),
            new WeaponAbility("cursed", 666, 666, 1f)
    };

    private Storage storage = Storage.getInstance();

    public Weapon createWeapon() {
        Weapon weapon = newWeapon();
        storage.addNewEntity(weapon);
        String type = weaponTypes[rnd.nextInt(weaponTypes.length)];
        WeaponAbility ability = weaponAbilities[rnd.nextInt(weaponAbilities.length)];
        String color = colors[rnd.nextInt(colors.length)];
        weapon.setTitle(ability.description + " " + color + " " + type);
        weapon.setDamage(ability.getDamage(rnd));
        weapon.setDropChance(ability.dropChance);
        return weapon;
    }

    public Weapon newWeapon() {
        return new Weapon();
    }


    public Place createPlace () {
        Place place = newPlace();
        storage.addNewEntity(place);
        String color = colors[rnd.nextInt(colors.length)];
        String size = sizes[rnd.nextInt(sizes.length)];
        String terrain = terrains[rnd.nextInt(terrains.length)];
        String descriptor = descriptors[rnd.nextInt(descriptors.length)];
        place.setDescription(size + " " + descriptor + " " + color + " " + terrain);
        place.setTitle(descriptor + " " + terrain);
        if (rnd.nextInt(100) < 50) {
            place.getInventory().addItem(createWeapon());
        }
        return place;
    }

    public Place newPlace() {
        return new Outdoors();
    }

    public Path createPath() {
        Path path = newPath();
        storage.addNewEntity(path);
        String color = colors[rnd.nextInt(colors.length)];
        String type = pathTypes[rnd.nextInt(pathTypes.length)];
        path.setTitle(color + " " + type);
        return path;
    }

    public Path newPath() {
        return new Path();
    }

    public Player createPlayer() {
        Player player = newPlayer();
        storage.addNewEntity(player);
        return player;
    }

    public Player newPlayer() {
        return new Player();
    }


}
