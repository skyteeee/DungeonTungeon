package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.entities.*;
import com.skyteeee.tungeon.entities.Character;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Storage;

import javax.swing.text.PlainView;
import java.util.Random;

public class EntityFactory {
    public static Random rnd = new Random();
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

    static String[] enemyDescriptors = new String[] {
            "grumpy",
            "sad",
            "furious",
            "joyful",
            "dumb",
            "tired",
            "dull",
            "hyper",
            "stormy",
            "sly",
            "crazy",
            "scary",
            "spooky",
            "tired of life"
    };

    static String[] enemyNames = new String[] {
            "ogre",
            "elf",
            "dwarf",
            "gnome",
            "worm",
            "rabbit",
            "tree",
            "dragon",
            "golem",
            "light entity",
            "ghost",
            "bear",
            "rat",
            "ancient wizard advisor",
            "wizard",
            "the one who must not be named"
    };

    static String[] armorMaterials = new String[] {
            "bronze",
            "obsidian",
            "golden",
            "chainmail",
            "magic",
            "leather"
    };


    static class ArmorAbility {
        String description;
        int[] defenceRange = new int[2];
        float[] absorptionRange = new float[2];
        float dropChance;

        ArmorAbility(String description, int defMin, int defMax, float absMin, float absMax, float dropChance) {
            this.description = description;
            defenceRange[0] = defMin;
            defenceRange[1] = defMax;
            absorptionRange[0] = absMin;
            absorptionRange[1] = absMax;
            this.dropChance = dropChance;
        }

        int getDefence(Random rnd) {
            return rnd.nextInt(defenceRange[0], defenceRange[1]);
        }

        float getAbsorption(Random rnd) {
            return rnd.nextFloat(absorptionRange[0], absorptionRange[1]);
        }

    }

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

    static ArmorAbility[] armorAbilities = new ArmorAbility[] {
            new ArmorAbility("rusty", 1, 3, 0.95f, 1.0f, 0.2f),
            new ArmorAbility("thin", 2, 7, 0.9f, 1.0f, 0.2f),
            new ArmorAbility("glorious", 10, 25, 0.75f, 0.9f, 0.3f),
            new ArmorAbility("polished", 10, 35, 0.5f, 0.8f, 0.5f),
            new ArmorAbility("legendary", 30, 100, 0.5f, 0.8f, 0.5f),

    };

    private Storage storage = Storage.getInstance();

    private static final int WEAPON_CHANCE = 50;
    private static final int ENEMY_CHANCE = 40;
    private static final int ARMOR_CHANCE = 80;

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

    public Armor createArmor() {
        Armor armor = newArmor();
        storage.addNewEntity(armor);
        ArmorAbility ability = armorAbilities[rnd.nextInt(armorAbilities.length)];
        String material = armorMaterials[rnd.nextInt(armorMaterials.length)];
        armor.setTitle(ability.description + " " + material + " armor");
        armor.setDefence(ability.getDefence(rnd));
        armor.setAbsorption(ability.getAbsorption(rnd));
        armor.setDropChance(ability.dropChance);
        return armor;
    }

    public Armor newArmor() {
        return new Armor();
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
        if (rnd.nextInt(100) < WEAPON_CHANCE) {
            place.getInventory().addItem(createWeapon());
        }

        if (rnd.nextInt(100) < ARMOR_CHANCE) {
            place.getInventory().addItem(createArmor());
        }

        if (rnd.nextInt(100) < ENEMY_CHANCE) {
            Enemy enemy = createEnemy();
            enemy.setCurrentPlace(place);
        }

        return place;
    }

    public Place newPlace() {
        return new Outdoors();
    }

    public Enemy createEnemy() {
        Enemy enemy = newEnemy();
        storage.addNewEntity(enemy);
        enemy.setTitle(enemyDescriptors[rnd.nextInt(enemyDescriptors.length)] + " " + enemyNames[rnd.nextInt(enemyNames.length)]);
        enemy.getInventory().addItem(createWeapon());
        return enemy;
    }

    public Enemy newEnemy() {
        return new Enemy();
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
