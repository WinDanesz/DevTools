package com.windanesz.devtools;

import java.util.Random;

public final class WorldNameGenerator {

    private static final String[] ADJECTIVES = {
            "ancient", "azure", "blazing", "bold", "carved", "cobalt", "creeping",
            "dark", "deep", "drifting", "eerie", "emerald", "endless", "fierce",
            "frozen", "glowing", "golden", "grand", "hidden", "hollow", "icy",
            "iron", "jade", "jagged", "keen", "lost", "lush", "misty", "mossy",
            "narrow", "obsidian", "onyx", "phantom", "prickly", "quiet", "radiant",
            "roaming", "rugged", "scarlet", "silent", "stony", "stormy", "thorny",
            "twilight", "umbral", "verdant", "vivid", "wandering", "wicked", "wild"
    };

    private static final String[] NOUNS = {
            "axolotl", "badger", "bat", "bear", "blaze", "boar", "burrow",
            "canyon", "cave", "cobweb", "crane", "creeper", "deer", "dragon",
            "elk", "ender", "enderman", "falcon", "ferret", "fox", "frog",
            "gecko", "ghast", "goat", "hawk", "husk", "ibis", "jaguar",
            "keeper", "llama", "lynx", "mole", "moose", "newt", "owl",
            "panda", "parrot", "phantom", "rabbit", "ravine", "raven",
            "slime", "spider", "turtle", "viper", "witch", "wither", "wolf",
            "zombie"
    };

    private static final Random RANDOM = new Random();

    private WorldNameGenerator() {}

    public static String generate() {
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];
        return adjective + "-" + noun;
    }
}
