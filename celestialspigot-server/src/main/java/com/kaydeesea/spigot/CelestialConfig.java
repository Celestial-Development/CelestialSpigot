package com.kaydeesea.spigot;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.kaydeesea.spigot.hitdetection.LagCompensator;
import com.kaydeesea.spigot.threads.impl.HitDetectionThread;
import com.kaydeesea.spigot.threads.impl.KnockbackThread;
import com.kaydeesea.spigot.util.YamlCommenter;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

@Getter
@Setter
public class CelestialConfig {

    private static final String HEADER = "This is the main configuration file for CelestialSpigot.\n"
                                         + "Modify with caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;


    private YamlCommenter c;
    // from
    private String pingCommandSelf;
    private String pingCommandOther;
    private String nightCommand;
    private String dayCommand;
    private String opGiveCommand;
    private String opTakeCommand;
    private String setSlotsCommand;
    private String killEntitiesCommand;

    private ArrayList<String> tpsCommand;
    private ArrayList<String> versionCommand;
    private ArrayList<String> opCommand;

    private boolean threadAffinity;
    private boolean checkForMalware;
    private boolean kickForSpam;
    private boolean showPlayerIps;
    private boolean instantRespawn;

    private boolean enableDayCommand;
    private boolean enableNightCommand;
    private boolean enablePluginsCommand;
    private boolean enableVersionCommand;
    private boolean enableKillEntitiesCommand;
    private boolean enableReloadCommand;
    private boolean enablePingCommand;
    private boolean enableSetSlotsCommand;
    private boolean enablePluginCommand;
    private boolean enableShrugCommand;

    private boolean improvedHitDetection;
    private boolean smoothTeleportation;
    private boolean optimizeTNTMovement;
    private boolean optimizeLiquidExplosions;
    private int timeUpdateFrequency;

    private boolean firePlayerMoveEvent;
    private boolean fireLeftClickAir;
    private boolean fireLeftClickBlock;
    private boolean fireLeafDecayEvent;
    private boolean fireEntityExplodeEvent;

    private boolean entityActivation;
    private boolean invalidArmAnimationKick;
    private boolean mobAIEnabled;
    private boolean blockOperations;
    private boolean disableJoinMessage;
    private boolean disableLeaveMessage;
    private boolean tabCompletePlugins;
    private boolean tcpNoDelay;
    private boolean usePandaWire;

    private int minSpawnDelay;
    private int maxSpawnDelay;
    private int spawnCount;
    private int spawnRange;
    private int maxNearbyEntities;
    private int requiredPlayerRange;

    private boolean fixEatWhileRunning;
    private boolean relativeMoveFix;
    private boolean fixArmorDamage;
    private boolean fixArrowBounceGlitch;
    private boolean fixSuffocationGlitch;
    private boolean fixDoubleHitBug;
    private boolean fixBlockHitGlitch;
    private boolean fixBlockHitAnimationGlitch;

    private double criticalDamageMultiplier;
    private boolean toggleFallDamageKB;
    private boolean fireArmAnimationEvent;
    private boolean optimizeArmSwings;

    private int targetTPS;
    private int pickupDelay;

    private int chunkLoadingThreads;
    private boolean doChunkUnload;
    private int playersPerThread;

    private float potionThrowMultiplier;
    private float potionThrowOffset;
    private float potionFallSpeed;

    private float pearlSpeed;
    private float pearlGravity;
    private float pearlVerticalOffset;
    private boolean pearlDamage;


    public CelestialConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("version.properties");
        Properties prop = new Properties();

        try {
            prop.load(is);
            CelestialBridge.version = (String) prop.getOrDefault("version", "Unknown");
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        CelestialSpigot.INSTANCE.setLagCompensator(new LagCompensator());
        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();
        this.c = new YamlCommenter();
        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            System.out.println("Generating a new settings.yml file.");
        } catch (InvalidConfigurationException ex) {
            System.out.println("Could not load settings.yml, please correct your syntax errors");
            ex.printStackTrace();
            throw Throwables.propagate(ex);
        }

        this.config.options().copyDefaults(true);

        this.loadConfig();
    }

    private void loadConfig() {


        ArrayList<String> tpsCMD = new ArrayList<>();
        tpsCMD.add("&b&lPERFORMANCE&7:");
        tpsCMD.add(" ");
        tpsCMD.add("&bUptime: &f%uptime%");
        tpsCMD.add("&bTPS: &f%tps%");
        tpsCMD.add("&bLag: &f%lag%");
        tpsCMD.add(" ");
        tpsCMD.add("&bEntities: &f%entities%");
        tpsCMD.add("&bChunks: &f%loadedChunks%");
        tpsCMD.add("&bMemory: &f%usedMemory%/%allocatedMemory% MB");
        tpsCMD.add(" ");

        this.tpsCommand = new ArrayList<String> (this.getList("messages.tps-command", tpsCMD));

        ArrayList<String> versionCMD = new ArrayList<>();
        versionCMD.add(" ");
        versionCMD.add("&b&lCelestial&3&lSpigot Info");
        versionCMD.add(" ");
        versionCMD.add("&bVersion: &7%version%");
        versionCMD.add("&bAuthor: &7KayDeeSea");
        versionCMD.add("&bDiscord: &3https://discord.gg/9cWCcExTWh");
        versionCMD.add(" ");

        this.versionCommand = new ArrayList<String>(this.getList("messages.version-command", versionCMD));

        ArrayList<String> opCMD = new ArrayList<>();
        opCMD.add("&3&m--------&7&m" + StringUtils.repeat("-", 37) + "&3&m--------");
        opCMD.add("&b&lCelestial&3&lSpigot &7(OP Commands)");
        opCMD.add("&3&m--------&7&m" + StringUtils.repeat("-", 37) + "&3&m--------");
        opCMD.add("&7 * &3/op &8<&7player&8> &8(&7&oGives a player operator status&8)");
        opCMD.add("&7 * &3/deop &8<&7player&8> &8(&7&oRemoves a player's operator status&8)");
        opCMD.add("&3&m--------&7&m" + StringUtils.repeat("-", 37) + "&3&m--------");


        this.opCommand = new ArrayList<String>(this.getList("messages.op-command", opCMD));

        this.pingCommandSelf = this.getString("messages.ping-command-self", "&bYour ping is: &3%ping%");
        this.pingCommandOther = this.getString("messages.ping-command-other", "&b%player%'s ping is: &3%ping%");
        this.nightCommand = this.getString("messages.night-command", "&bTime has been set to &3night &b in your world.");
        this.dayCommand = this.getString("messages.day-command", "&bTime has been set to &3day &b in your world.");
        this.opGiveCommand = this.getString("messages.op-command-give", "&7You've granted &3%player% &7permissions for &3operator&7!");
        this.opTakeCommand = this.getString("messages.op-command-take", "&7You've taken &3%player%'s &7permissions for &3operator&7!");
        this.setSlotsCommand = this.getString("messages.set-slots-command", "&6Slots updated to &e%slots%");
        this.killEntitiesCommand = this.getString("messages.kill-entities-command", "&aYou have removed a total of &7%entities% &aentities");

        this.mobAIEnabled = this.getBoolean("server.mob-ai", true);
        this.targetTPS = this.getInt("server.target-tps", 20);
        this.threadAffinity = this.getBoolean("server.thread-affinity", false);
        this.checkForMalware = this.getBoolean("server.check-for-malware", true);
        this.showPlayerIps = this.getBoolean("server.show-player-ips", true);
        this.disableJoinMessage = this.getBoolean("server.disable-join-message", false);
        this.disableLeaveMessage = this.getBoolean("server.disable-leave-message", false);
        this.smoothTeleportation = this.getBoolean("server.smooth-teleportation", true);


        this.instantRespawn = this.getBoolean("instant-respawn", true);
        this.kickForSpam = this.getBoolean("kick-for-spam", false);

        this.enableNightCommand = this.getBoolean("commands.enable-night-command", true);
        this.enablePingCommand = this.getBoolean("commands.enable-ping-command", true);
        this.enableDayCommand = this.getBoolean("commands.enable-day-command", true);
        this.enableVersionCommand = this.getBoolean("commands.enable-version-command", true);
        this.enableReloadCommand = this.getBoolean("commands.enable-reload-command", true);
        this.enablePluginsCommand = this.getBoolean("commands.enable-plugins-command", true);
        this.enableKillEntitiesCommand = this.getBoolean("commands.enable-kill-entities-command", true);
        this.enableSetSlotsCommand = this.getBoolean("commands.enable-set-slots-command", true);
        this.enablePluginCommand = this.getBoolean("commands.enable-plugin-command", true);
        this.enableShrugCommand = this.getBoolean("commands.enable-shrug-command", true);

        this.improvedHitDetection = this.getBoolean("improved-hit-detection", true);
        this.optimizeTNTMovement = this.getBoolean("optimize-tnt-movement", true);
        this.optimizeLiquidExplosions = this.getBoolean("optimize-liquid-explosions", true);
        this.timeUpdateFrequency = this.getInt("time-update-frequency", 100);

        this.entityActivation = this.getBoolean("entity-activation", false);
        this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", true);
        this.blockOperations = this.getBoolean("block-operations", false);
        this.tabCompletePlugins = this.getBoolean("tab-complete-plugins", false);
        this.tcpNoDelay = this.getBoolean("tcp-no-delay", true);
        this.usePandaWire = this.getBoolean("use-panda-wire", true);

        this.firePlayerMoveEvent = this.getBoolean("fire.player-move-event", true);
        this.fireLeftClickAir = this.getBoolean("fire.left-click-air", true);
        this.fireLeftClickBlock = this.getBoolean("fire.left-click-block", true);
        this.fireLeafDecayEvent = this.getBoolean("fire.leaf-decay-event", true);
        this.fireEntityExplodeEvent = this.getBoolean("fire.entity-explode-event", true);
        this.fireArmAnimationEvent = this.getBoolean("fire.arm-animation-event", true);


        this.fixEatWhileRunning = this.getBoolean("fix.eat-while-running", true);
        this.relativeMoveFix = this.getBoolean("fix.relative-move", true);
        this.fixArmorDamage = this.getBoolean("fix.armor-damage", true);
        this.fixArrowBounceGlitch = this.getBoolean("fix.arrow-bounce-glitch", true);
        this.fixDoubleHitBug = this.getBoolean("fix.double-hit-bug", true);
        this.fixBlockHitGlitch = this.getBoolean("fix.block-hit-glitch", true);
        this.fixBlockHitAnimationGlitch = this.getBoolean("fix.block-hit-animation", true);
        this.fixSuffocationGlitch = this.getBoolean("fix.suffocation-glitch", true);


        this.criticalDamageMultiplier = this.getDouble("critical-damage-multiplier", 1.5);
        this.toggleFallDamageKB = this.getBoolean("toggle-fall-damage-kb", false);
        this.optimizeArmSwings = this.getBoolean("optimize-arm-swings", true);
        this.pickupDelay = this.getInt("pickup-delay", 40);

        this.chunkLoadingThreads = this.getInt("chunks.chunk-loading-threads", 2);
        this.doChunkUnload = this.getBoolean("chunks.do-chunk-unload", true);
        this.playersPerThread = this.getInt("chunks.players-per-thread", 50);

        this.minSpawnDelay = this.getInt("spawners.min-spawn-delay", 200);
        this.maxSpawnDelay = this.getInt("spawners.max-spawn-delay", 800);
        this.spawnCount = this.getInt("spawners.spawn-count", 4);
        this.spawnRange = this.getInt("spawners.spawn-range", 4);
        this.maxNearbyEntities = this.getInt("spawners.max-nearby-entities", 6);
        this.requiredPlayerRange = this.getInt("spawners.required-player-range", 16);

        this.potionThrowMultiplier = this.getFloat("potions.potion-throw-multiplier", 0.5f);
        this.potionThrowOffset = this.getFloat("potions.potion-throw-offset", -10.0f);
        this.potionFallSpeed = this.getFloat("potions.potion-fall-speed", 0.05f);

        this.pearlDamage = this.getBoolean("pearls.pearl-damage", true);
        this.pearlGravity = this.getFloat("pearls.pearl-gravity", 0.03F);
        this.pearlSpeed = this.getFloat("pearls.pearl-speed", 1.5F);
        this.pearlVerticalOffset = this.getFloat("pearls.vertical-offset", 0.0F);

        setVariables();
        save();
    }
    private KnockbackThread knockbackThread;
    private HitDetectionThread hitDetectionThread;

    public void setVariables() {
        ChunkIOExecutor.BASE_THREADS = chunkLoadingThreads;
        ChunkIOExecutor.PLAYERS_PER_THREAD = playersPerThread;

        MinecraftServer.TPS = getTargetTPS();
        MinecraftServer.TICK_TIME = 1_000_000_000 / MinecraftServer.TPS;
    }
    public void loadComments() {
        c.setHeader(HEADER);
        // add messages
        c.addComment("messages", "Modify current commands messages");
        c.addComment("messages.tps-command", "Modify current tps command message");
        c.addComment("messages.ping-command-self", "Main message when executing /ping");
        c.addComment("messages.ping-command-other", "Main message when executing /ping (player)");
        c.addComment("messages.version-command", "Modify version command message");
        c.addComment("messages.day-command", "Modify day command message");
        c.addComment("messages.night-command", "Modify night command message");
        c.addComment("messages.op-command", "Modify op/deop command message");
        c.addComment("messages.op-command-give", "Modify op give command message");
        c.addComment("messages.op-command-take", "Modify op take command message");
        c.addComment("messages.set-slots-command", "Modify set slots command message");
        c.addComment("messages.kill-entities-command", "Modify kill entities command message");

        // Add comments for mob AI setting
        c.addComment("server", "Some settings about spigot features.");
        c.addComment("server.mob-ai", "Setting to enable or disable mob artificial intelligence");
        c.addComment("server.target-tps", "What TPS should the server target? 'Do not set this above 200 unless you want your server to explode.' - CarbonSpigot");
        c.addComment("server.thread-affinity", "When this is true, it allocates an entire cpu core to the server, it improves performance but uses more cpu.");
        c.addComment("server.check-for-malware", "Enables checking for malwares");
        c.addComment("server.show-player-ips", "Enable or disable showing player ips in console.");
        c.addComment("server.disable-join-message", "Enable or disable join messages");
        c.addComment("server.disable-leave-message", "Enable or disable leave messages");
        c.addComment("server.smooth-teleportation", "Should smooth teleportation be enabled? (PandaSpigot)");

        // Add comments for command-related settings
        c.addComment("commands", "Toggle commands");
        c.addComment("commands.enable-ping-command", "Enable or disable the ping command");
        c.addComment("commands.enable-version-command", "Enable or disable the version command");
        c.addComment("commands.enable-reload-command", "Enable or disable the reload command");
        c.addComment("commands.enable-plugins-command", "Enable or disable the plugins command");
        c.addComment("commands.enable-day-command", "Enable or disable the day command");
        c.addComment("commands.enable-night-command", "Enable or disable the night command");
        c.addComment("commands.enable-kill-entities-command", "Enable or disable the kill entities command (command that kills all entities of the specified type)");
        c.addComment("commands.enable-set-slots-command", "Enable or disable the set slots command (command that changes the player slots ingame)");
        c.addComment("commands.enable-plugin-command", "Enable or disable the plugin command (command that lets you manage plugins ingame)");
        c.addComment("commands.enable-shrug-command", "Enable or disable the shrug command (¯\\_(ツ)_/¯)");

        // Event related Settings
        c.addComment("fire", "Controls whether certain performance-intensive events are enabled. Disabling them may improve server performance.");
        c.addComment("fire.player-move-event", "Enable firing events when players move");
        c.addComment("fire.left-click-air", "Enable firing events when players left-click air");
        c.addComment("fire.left-click-block", "Enable firing events when players left-click blocks");
        c.addComment("fire.leaf-decay-event", "Enable firing events when leaf decays");
        c.addComment("fire.entity-explode-event", "Enable firing events when a TNT/Fireball/creeper explodes");
        c.addComment("fire.arm-animation-event", "Toggles the PlayerArmAnimationEvent.");

        // Add comments for entity and game settings
        c.addComment("instant-respawn", "Enable or disable instant respawning");
        c.addComment("kick-for-spam", "Setting to disable disconnect.spam message when spamming");
        c.addComment("entity-activation", "Enable or disable entity activation rules");
        c.addComment("invalid-arm-animation-kick", "Kick players for invalid arm animations");
        c.addComment("optimize-tnt-movement", "Should we use panda spigot optimizations for tnt movement?");
        c.addComment("time-update-frequency", "How many ticks in between sending time updates to players? (PandaSpigot)");
        c.addComment("optimize-liquid-explosions", "Should we use panda spigot optimizations for liquid explosions?");
        c.addComment("block-operations", "Enable or disable block operations");
        c.addComment("improved-hit-detection", "Toggle improved hit detection, This makes the calculation of locations faster while PvPing.");
        c.addComment("tab-complete-plugins", "Should plugins be tab completed when /version?");
        c.addComment("tcp-no-delay", "Should tcp no delay be enabled in minecraft server connection?");
        c.addComment("use-panda-wire", "Should we use the panda wire algorithm to handle redstone wires?");

        c.addComment("chunks.chunk-loading-threads", "Change the chunk loading threads");
        c.addComment("chunks.players-per-thread", "Change the max players per chunk thread");
        c.addComment("chunks.do-chunk-unload", "Enable or disable chunk unloading");

        c.addComment("pickup-delay", "Change the dropped item pickup delay");
        c.addComment("critical-damage-multiplier", "Critical damage multiplier. This is part of the new spigot's damage calculations");

        // Section: Fixes.
        c.addComment("fix", "Toggles a list of available patches");
        c.addComment("fix.eat-while-running", "Fixes the bug that makes players eat while running");
        c.addComment("fix.relative-move", "Fixes a calculation bug where MathHelper#floor was being used for an entity's placement (Credits: JT - PvPLand Developer)");
        c.addComment("fix.armor-damage", "This option fixes the armor damage to be less than expected");
        c.addComment("fix.arrow-bounce-glitch", "Fixes the arrow bounce glitch that comes from disable 'keep-spawn-in-memory' for a certain world.");
        c.addComment("fix.suffocation-glitch", "When you pearl inside a block/get stuck inside a block while falling, you get damaged and you fall faster.");
        c.addComment("fix.double-hit-bug", "Toggle to fix the \"double hit\" (or \"high damage\") bug that allows damage during no-damage ticks, causing excessive knockback and unintended fly-outs.");
        c.addComment("fix.block-hit-glitch", "This fixes block hit glitch server side when you attack a player.");
        c.addComment("fix.block-hit-animation", "This fixes block hit glitch client-side when you swing your arm");

        c.addComment("toggle-fall-damage-kb", "Toggles fall damage knockback");
        c.addComment("optimize-arm-swings", "Optimizes arm swings. (Credit: CarbonSpigot)");

        // Section: Spawners configuration
        c.addComment("spawners", "Configuration settings for entity spawners.");
        c.addComment("spawners.min-spawn-delay", "Minimum delay (in ticks) between spawns. Lower values make spawners faster.");
        c.addComment("spawners.max-spawn-delay", "Maximum delay (in ticks) between spawns. Higher values make spawners slower.");
        c.addComment("spawners.spawn-count", "Number of entities a spawner will spawn per activation.");
        c.addComment("spawners.spawn-range", "The radius (in blocks) around the spawner where entities can appear.");
        c.addComment("spawners.max-nearby-entities", "Maximum number of entities near the spawner. Spawner stops if exceeded.");
        c.addComment("spawners.required-player-range", "Distance (in blocks) players must be from the spawner for it to activate.");

        // Add comments for potion-related settings
        c.addComment("potions", "Change the potions settings (/potion)");
        c.addComment("potions.potion-throw-multiplier", "Set the multiplier for potion throwing speed");
        c.addComment("potions.potion-throw-offset", "Set the offset angle for potion throws");
        c.addComment("potions.potion-fall-speed", "Set the falling speed for potions");

        // add Comments for pearls
        c.addComment("pearls", "Edit pearls settings");
        c.addComment("pearls.pearl-damage", "Should pearls do damage?");
        c.addComment("pearls.pearl-gravity", "Gravity of pearls");
        c.addComment("pearls.pearl-speed", "Speed of pearls");
        c.addComment("pearls.vertical-offset", "Pearls vertical offset");
    }

    public void save() {
        new Thread(() -> {
            try {
                this.config.save(this.configFile);
                loadComments();
                this.c.saveComments(this.configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, def);
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, def);
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, def);
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, def);
    }

}
