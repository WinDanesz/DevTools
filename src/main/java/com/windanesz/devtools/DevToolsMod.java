package com.windanesz.devtools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.Random;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, clientSideOnly = true)
public class DevToolsMod {

    private static final int CREATE_TEST_WORLD_BUTTON_ID = 1725;
    private static final String KEY_CATEGORY = "key.categories.devtools";

    private static final KeyBinding CREATIVE_MODE_KEY = new KeyBinding("key.devtools.creative", Keyboard.KEY_F7, KEY_CATEGORY);
    private static final KeyBinding SURVIVAL_MODE_KEY = new KeyBinding("key.devtools.survival", Keyboard.KEY_F6, KEY_CATEGORY);
    private static final KeyBinding SPECTATOR_MODE_KEY = new KeyBinding("key.devtools.spectator", Keyboard.KEY_F8, KEY_CATEGORY);
    private static final KeyBinding TOGGLE_DAY_NIGHT_KEY = new KeyBinding("key.devtools.toggle_day_night", Keyboard.KEY_F9, KEY_CATEGORY);
    private static final KeyBinding HEAL_KEY = new KeyBinding("key.devtools.heal", Keyboard.KEY_F10, KEY_CATEGORY);
    private static final KeyBinding REPEAT_COMMAND_KEY = new KeyBinding("key.devtools.repeat_command", Keyboard.KEY_F12, KEY_CATEGORY);

    private boolean isDay = true;
    private String lastCommand = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClientRegistry.registerKeyBinding(CREATIVE_MODE_KEY);
        ClientRegistry.registerKeyBinding(SURVIVAL_MODE_KEY);
        ClientRegistry.registerKeyBinding(SPECTATOR_MODE_KEY);
        ClientRegistry.registerKeyBinding(TOGGLE_DAY_NIGHT_KEY);
        ClientRegistry.registerKeyBinding(HEAL_KEY);
        ClientRegistry.registerKeyBinding(REPEAT_COMMAND_KEY);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMainMenuInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiMainMenu)) {
            return;
        }

        GuiButton singleplayerButton = null;
        for (GuiButton button : event.getButtonList()) {
            if (button.id == 1) {
                singleplayerButton = button;
                break;
            }
        }

        if (singleplayerButton == null) {
            return;
        }

        singleplayerButton.x = event.getGui().width / 2 - 100;
        singleplayerButton.width = 98;
        event.getButtonList().add(new GuiButton(
                CREATE_TEST_WORLD_BUTTON_ID,
                event.getGui().width / 2 + 2,
                singleplayerButton.y,
                98,
                20,
                I18n.format("menu.devtools.create_test_world")));
    }

    @SubscribeEvent
    public void onMainMenuButtonPressed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == CREATE_TEST_WORLD_BUTTON_ID) {
            createAndLaunchTestWorld();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (CREATIVE_MODE_KEY.isPressed()) {
            switchGameMode("creative");
        }
        if (SURVIVAL_MODE_KEY.isPressed()) {
            switchGameMode("survival");
        }
        if (SPECTATOR_MODE_KEY.isPressed()) {
            switchGameMode("spectator");
        }
        if (TOGGLE_DAY_NIGHT_KEY.isPressed()) {
            toggleDayNight();
        }
        if (HEAL_KEY.isPressed()) {
            heal();
        }
        if (REPEAT_COMMAND_KEY.isPressed() && lastCommand != null) {
            Minecraft.getMinecraft().player.sendChatMessage(lastCommand);
        }
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        if (event.getMessage().startsWith("/")) {
            lastCommand = event.getMessage();
        }
    }

    private static void switchGameMode(String gameModeName) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player != null) {
            minecraft.player.sendChatMessage("/gamemode " + gameModeName);
        }
    }

    private void toggleDayNight() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player != null) {
            isDay = !isDay;
            minecraft.player.sendChatMessage("/time set " + (isDay ? "day" : "night"));
        }
    }

    private static void heal() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player != null) {
            minecraft.player.sendChatMessage("/effect @s minecraft:instant_health 1 255 true");
            minecraft.player.sendChatMessage("/effect @s minecraft:saturation 1 255 true");
        }
    }

    private static void createAndLaunchTestWorld() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ISaveFormat saveFormat = minecraft.getSaveLoader();
        String worldName = WorldNameGenerator.generate();
        String worldSaveName = createUniqueWorldSaveName(saveFormat, worldName);
        long seed = new Random().nextLong();
        WorldSettings worldSettings = new WorldSettings(seed, GameType.CREATIVE, true, false, WorldType.FLAT)
                .enableCommands()
                .setGeneratorOptions(FlatGeneratorInfo.getDefaultFlatGenerator().toString());

        writeTestWorldInfo(minecraft, worldSaveName, worldName, seed);

        minecraft.displayGuiScreen(null);
        minecraft.launchIntegratedServer(worldSaveName, worldName, worldSettings);
    }

    private static void writeTestWorldInfo(Minecraft minecraft, String worldSaveName, String worldDisplayName, long seed) {
        ISaveFormat saveFormat = new AnvilSaveConverter(new File(minecraft.gameDir, "saves"), null);
        ISaveHandler saveHandler = saveFormat.getSaveLoader(worldSaveName, false);

        NBTTagCompound worldData = new NBTTagCompound();
        worldData.setLong("RandomSeed", seed);
        worldData.setString("generatorName", "flat");
        worldData.setString("generatorOptions", FlatGeneratorInfo.getDefaultFlatGenerator().toString());
        worldData.setInteger("generatorVersion", 0);
        worldData.setInteger("GameType", GameType.CREATIVE.getID());
        worldData.setBoolean("MapFeatures", true);
        worldData.setBoolean("hardcore", false);
        worldData.setBoolean("allowCommands", true);
        worldData.setBoolean("initialized", true);
        worldData.setString("LevelName", worldDisplayName);
        worldData.setLong("Time", 6000L);
        worldData.setLong("DayTime", 6000L);
        worldData.setInteger("SpawnX", 0);
        worldData.setInteger("SpawnY", 4);
        worldData.setInteger("SpawnZ", 0);
        worldData.setInteger("clearWeatherTime", Integer.MAX_VALUE);
        worldData.setInteger("rainTime", 0);
        worldData.setBoolean("raining", false);
        worldData.setInteger("thunderTime", 0);
        worldData.setBoolean("thundering", false);

        NBTTagCompound gameRules = new NBTTagCompound();
        gameRules.setString("doDaylightCycle", "false");
        gameRules.setString("doMobSpawning", "false");
        gameRules.setString("doWeatherCycle", "false");
        worldData.setTag("GameRules", gameRules);

        minecraft.loadWorld((WorldClient) null);
        System.gc();
        saveHandler.saveWorldInfo(new WorldInfo(worldData));
        saveFormat.flushCache();
    }

    static String createUniqueWorldSaveName(ISaveFormat saveFormat, String worldName) {
        String worldSaveName = sanitizeWorldSaveName(worldName);

        while (saveFormat.getWorldInfo(worldSaveName) != null) {
            worldSaveName = worldSaveName + "-";
        }

        return worldSaveName;
    }

    static String sanitizeWorldSaveName(String worldName) {
        String worldSaveName = worldName == null ? "" : worldName.trim();

        for (char illegalCharacter : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
            worldSaveName = worldSaveName.replace(illegalCharacter, '_');
        }

        worldSaveName = worldSaveName.replace('.', '_');

        if (worldSaveName.isEmpty()) {
            worldSaveName = "World";
        }

        return worldSaveName;
    }
}
