package com.skyteeee.tungeon.utils;

import com.skyteeee.tungeon.entities.*;
import com.skyteeee.tungeon.entities.items.Armor;
import com.skyteeee.tungeon.entities.items.Weapon;
import com.skyteeee.tungeon.storage.Storage;

import java.util.List;
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

        int getDefence(Random rnd, int level) {
            int initDef = rnd.nextInt(defenceRange[0], defenceRange[1]);
            return initDef + (int) (initDef * level * 0.07912);
        }

        float getAbsorption(Random rnd, int level) {
            float initAbs = rnd.nextFloat(absorptionRange[0], absorptionRange[1]);
            return initAbs - (initAbs * level * 0.0791f);
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

        int getDamage(Random rnd, int level) {
            int initDamage = rnd.nextInt(damageRange[0], damageRange[1]+1);
            int damage = initDamage + (int) (initDamage * level * 0.112);
            return damage;
        }

    }

    static class CursedAbility extends WeaponAbility {

        CursedAbility(String desc, int dmgMin, int dmgMax, float dropChance) {
            super(desc, dmgMin, dmgMax, dropChance);
        }

        @Override
        int getDamage(Random rnd, int level) {
            int damage = 0;

            for (int i = 0; i <= (level/4) + 1; i++) {
                damage += (int) (damageRange[0] * Math.pow(10, i));
            }
            return damage;
        }

    }

    static WeaponAbility[] weaponAbilities = new WeaponAbility[] {
            new WeaponAbility("weak", 1, 30, 0.2f),
            new WeaponAbility("mysterious", 5, 250, 0.6f),
            new WeaponAbility("strong", 25, 100, 0.4f),
            new WeaponAbility("great", 50, 150, 0.5f),
            new CursedAbility("cursed", 6, 6, 1f)
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
    private static final int ENEMY_CHANCE = 80;
    private static final int ARMOR_CHANCE = 40;

    public Weapon createWeapon(int level) {
        Weapon weapon = newWeapon();
        storage.addNewEntity(weapon);
        String type = weaponTypes[rnd.nextInt(weaponTypes.length)];
        WeaponAbility ability = weaponAbilities[rnd.nextInt(weaponAbilities.length)];
        String color = colors[rnd.nextInt(colors.length)];
        weapon.setTitle(ability.description + " " + color + " " + type);
        weapon.setDamage(ability.getDamage(rnd, level));
        weapon.setDropChance(ability.dropChance);
        weapon.setLevel(level);
        return weapon;
    }

    public void scatterWeapons(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllPlaces();
        places.remove(exclude);
        for (int i = 0; i < amount; i++) {
            Weapon weapon = createWeapon(level);
            Place place = places.get(rnd.nextInt(places.size()));
            place.take(weapon);
        }
    }

    public Weapon newWeapon() {
        return new Weapon();
    }

    public Armor createArmor(int level) {
        Armor armor = newArmor();
        storage.addNewEntity(armor);
        ArmorAbility ability = armorAbilities[rnd.nextInt(armorAbilities.length)];
        String material = armorMaterials[rnd.nextInt(armorMaterials.length)];
        armor.setTitle(ability.description + " " + material + " armor");
        armor.setDefence(ability.getDefence(rnd, level));
        armor.setAbsorption(ability.getAbsorption(rnd, level));
        armor.setDropChance(ability.dropChance);
        return armor;
    }

    public void scatterArmor(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllPlaces();
        places.remove(exclude);
        for (int i = 0; i < amount; i++) {
            Armor armor = createArmor(level);
            Place place = places.get(rnd.nextInt(places.size()));
            place.take(armor);
        }
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
            place.getInventory().addItem(createWeapon(1));
        }

        if (rnd.nextInt(100) < ARMOR_CHANCE) {
            place.getInventory().addItem(createArmor(1));
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
        return createEnemy(1);
    }

    public Enemy createEnemy(int level) {
        Enemy enemy = newEnemy(level);
        storage.addNewEntity(enemy);
        enemy.setTitle("LVL " + level + " " + enemyDescriptors[rnd.nextInt(enemyDescriptors.length)] + " " + enemyNames[rnd.nextInt(enemyNames.length)]);
        enemy.getInventory().addItem(createWeapon(level));
        enemy.setArmor(createArmor(level));
        return enemy;
    }

    public void scatterEnemies(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllPlaces();
        places.remove(exclude);
        for (int i = 0; i < amount; i++) {
            Enemy enemy = createEnemy(level);
            Place place = places.get(rnd.nextInt(places.size()));
            enemy.setCurrentPlace(place);
        }
    }

    public Enemy newEnemy(int level) {
        return new Enemy(level);
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
