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

    static EnemyAbility[] enemyDescriptors = new EnemyAbility[] {
            new EnemyAbility("grumpy", 0, 0, 0.6f),
            new EnemyAbility("sad", 0, 0, 0.2f),
            new EnemyAbility("furious", 20, 20, 0.9f),
            new EnemyAbility("joyful", 0, 0, 0.1f),
            new EnemyAbility("dumb", 0, 0, 0.5f),
            new EnemyAbility("tired", 0, 0, 0.1f),
            new EnemyAbility("dull", 0, 0, 0.5f),
            new EnemyAbility("hyper", 10, 10, 0.8f),
            new EnemyAbility("stormy", 0, 0, 0.9f),
            new EnemyAbility("sly", 0, 0, 0.5f),
            new EnemyAbility("crazy", 0, 0, 0.75f),
            new EnemyAbility("scary", 0, 0, 0.12f),
            new EnemyAbility("spooky", 0, 0, 0.1f),
            new EnemyAbility("tired of life", -20, -10, 0.01f)
    };

    static EnemyAbility[] enemyNames = new EnemyAbility[] {
            new EnemyAbility("ogre", 100, 150, 1),
            new EnemyAbility("elf", 50, 100, 1),
            new EnemyAbility("dwarf", 50, 75, 1),
            new EnemyAbility("gnome", 50, 80, 1),
            new EnemyAbility("worm", 10, 50, 1),
            new EnemyAbility("rabbit", 15, 45, 1),
            new EnemyAbility("tree", 100, 125, 1),
            new EnemyAbility("dragon", 100, 200, 1),
            new EnemyAbility("golem", 150, 175, 1),
            new EnemyAbility("light entity", 150, 250, 1),
            new EnemyAbility("ghost", 10, 25, 1),
            new EnemyAbility("bear", 30, 100, 1),
            new EnemyAbility("rat", 1, 30, 1),
            new EnemyAbility("ancient wizard advisor", 20, 150, 1),
            new EnemyAbility("wizard", 40, 120, 1),
            new EnemyAbility("the one who must not be named", 98, 201, 1)
    };

    static String[] armorMaterials = new String[] {
            "bronze",
            "obsidian",
            "golden",
            "chainmail",
            "magic",
            "leather"
    };

    static class EnemyAbility {
        String label;
        int[] healthRange = new int[2];
        float attackChance;
        EnemyAbility (String label, int healthMin, int healthMax, float attackChance) {
            this.label = label;
            healthRange[0] = healthMin;
            healthRange[1] = healthMax;
            this.attackChance = attackChance;
        }

        EnemyAbility combine(EnemyAbility target) {
            return new EnemyAbility(
                    label + " " + target.label,
                    healthRange[0] + target.healthRange[0],
                    healthRange[1] + target.healthRange[1],
                    attackChance * target.attackChance);
        }

        int getHealth(int level) {
            int health = EntityFactory.rnd.nextInt(healthRange[0], healthRange[1]);
            return health + (int) (health * (level-1) * 0.1);
        }

    }


    static class ArmorAbility {
        String description;
        int[] defenceRange = new int[2];
        float[] absorptionRange = new float[2];
        float dropChance;
        float durability;
        float resistance;

        ArmorAbility(String description, int defMin, int defMax, float absMin, float absMax, float dropChance, float resistance, float durability) {
            this.description = description;
            defenceRange[0] = defMin;
            defenceRange[1] = defMax;
            absorptionRange[0] = absMin;
            absorptionRange[1] = absMax;
            this.dropChance = dropChance;
            this.durability = durability;
            this.resistance = resistance;
        }

        int getDefence(Random rnd, int level) {
            int initDef = rnd.nextInt(defenceRange[0], defenceRange[1]);
            return initDef + (int) (initDef * level * 0.07912);
        }

        float getAbsorption(Random rnd, int level) {
            float initAbs = rnd.nextFloat(absorptionRange[0], absorptionRange[1]);
            return initAbs - (initAbs * level * 0.0791f);
        }

        float getDurability(int level) {
            return durability + (durability * level * 0.12f);
        }

    }

    static class WeaponAbility {
        String description;
        static final int MAX_DAMAGE = 100000;

        int[] damageRange = new int[2];
        float dropChance;
        float durability;
        float resistance;

        WeaponAbility(String desc, int dmgMin, int dmgMax, float dropChance, float resistance, float durability) {
            description = desc;
            damageRange[0] = dmgMin;
            damageRange[1] = dmgMax;
            this.dropChance = dropChance;
            this.resistance = resistance;
            this.durability = durability;
        }

        int getDamage(Random rnd, int level) {
            int initDamage = rnd.nextInt(damageRange[0], damageRange[1]+1);
            int damage = initDamage + (int) (initDamage * level * 0.112);
            return damage;
        }

        float getDurability(int level) {
            return durability + (durability * level * 0.1119f);
        }

    }

    static class CursedAbility extends WeaponAbility {

        CursedAbility(String desc, int dmgMin, int dmgMax, float dropChance) {
            super(desc, dmgMin, dmgMax, dropChance, 0.7f, 200);
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
            new WeaponAbility("weak", 1, 30, 0.2f, 0.8f, 500f),
            new WeaponAbility("mysterious", 5, 250, 0.6f, 0.75f, 250f),
            new WeaponAbility("strong", 25, 100, 0.4f, 0.6f, 350f),
            new WeaponAbility("great", 50, 150, 0.5f, 0.4f, 600f),
            new CursedAbility("cursed", 6, 6, 1f)
    };

    static ArmorAbility[] armorAbilities = new ArmorAbility[] {
            new ArmorAbility("rusty", 1, 3, 0.95f, 1.0f, 0.2f, 0.3f, 300),
            new ArmorAbility("thin", 2, 7, 0.9f, 1.0f, 0.2f, 0.4f, 350),
            new ArmorAbility("glorious", 10, 25, 0.75f, 0.9f, 0.3f, 0.59f, 300),
            new ArmorAbility("polished", 10, 35, 0.5f, 0.8f, 0.5f, 0.5f, 350),
            new ArmorAbility("legendary", 30, 100, 0.5f, 0.8f, 0.5f, 0.45f, 400),

    };

    private final Storage storage = Storage.getInstance();

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
        weapon.setDurability(ability.getDurability(level));
        weapon.setResistance(ability.resistance);
        weapon.setDropChance(ability.dropChance);
        weapon.setLevel(level);
        return weapon;
    }

    public void scatterWeapons(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllOfType(Place.class);
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
        armor.setLevel(level);
        ArmorAbility ability = armorAbilities[rnd.nextInt(armorAbilities.length)];
        String material = armorMaterials[rnd.nextInt(armorMaterials.length)];
        armor.setTitle(ability.description + " " + material + " armor");
        armor.setDefence(ability.getDefence(rnd, level));
        armor.setAbsorption(ability.getAbsorption(rnd, level));
        armor.setDurability(ability.getDurability(level));
        armor.setResistance(ability.resistance);
        armor.setDropChance(ability.dropChance);
        return armor;
    }

    public void scatterArmor(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllOfType(Place.class);
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
            for (int i = 0; i < 1; i++) {
                Enemy enemy = createEnemy();
                enemy.setCurrentPlace(place);
            }
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
        EnemyAbility ability = enemyDescriptors[rnd.nextInt(enemyDescriptors.length)]
                .combine(enemyNames[rnd.nextInt(enemyNames.length)]);
        enemy.setTitle("LVL " + level + " " + ability.label);
        enemy.setHealth(ability.getHealth(level));
        enemy.setAttackChance(ability.attackChance);
        enemy.getInventory().addItem(createWeapon(level));
        enemy.setArmor(createArmor(level));
        return enemy;
    }

    public void scatterEnemies(int amount, int level, Place exclude) {
        List<Place> places = storage.getAllOfType(Place.class);
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
