package me.jackfoley.sab;

import co.aikar.commands.PaperCommandManager;
import lombok.SneakyThrows;
import me.jackfoley.sab.chat.ChannelCommands;
import me.jackfoley.sab.chat.ChatWorker;
import me.jackfoley.sab.gamemode.GamemodeCommands;
import me.jackfoley.sab.history.HistoryCommands;
import me.jackfoley.sab.homes.HomeCommands;
import me.jackfoley.sab.listeners.*;
import me.jackfoley.sab.loops.BanLoop;
import me.jackfoley.sab.loops.MuteLoop;
import me.jackfoley.sab.messaging.MessagesCommand;
import me.jackfoley.sab.moderation.ModerationCommands;
import me.jackfoley.sab.reporting.ReportCommand;
import me.jackfoley.sab.sql.SQL;
import me.jackfoley.sab.teleporting.TeleportCommands;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Config;
import me.jackfoley.sab.util.EnderChestFile;
import me.jackfoley.sab.utilcommands.*;
import me.jackfoley.sab.worlds.WorldCommands;
import me.jackfoley.sab.worlds.WorldSetCommands;
import me.ryanhamshire.GriefPrevention.Claim;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static me.ryanhamshire.GriefPrevention.GriefPrevention.instance;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static final List<UUID> spy = new ArrayList<>();
    private static final HashMap<UUID, BukkitTask> timerMap = new HashMap<>();
    private static final HashMap<UUID, String> cfMap = new HashMap<>();
    private static SQL sql;

    private static Chat chat = null;

    // todo save last location for teleporting in survival.
    // todo make a /back command

    @SneakyThrows
    @Override
    public void onEnable() {

        if (false) {
            expiryRun(this);
            return;
        }


        System.out.println("================================");
        System.out.println("If the plugin is failing to load, \n" +
                            "make sure you have LuckPerms and Vault installed." +
                            "\nThe plugin will NOT work without them.");
        System.out.println("================================");
        plugin = this;
        new Config(this);
        sql = new SQL();
        sql.connect();
        sqlStuffs();
        setupChat();
        new EnderChestFile().setup(this);
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new ChannelCommands());
        manager.registerCommand(new ModerationCommands());
        manager.registerCommand(new MessagesCommand());
        manager.registerCommand(new TeleportCommands());
        manager.registerCommand(new FlyCommand());
        manager.registerCommand(new SabMC());
        manager.registerCommand(new ChatFilter());
        manager.registerCommand(new HomeCommands());
        manager.registerCommand(new ReportCommand());
        manager.registerCommand(new GamemodeCommands());
        manager.registerCommand(new WorldCommands());
        manager.registerCommand(new WorldSetCommands());
        manager.registerCommand(new HistoryCommands());
        manager.registerCommand(new HeadCommand());
        manager.registerCommand(new EnderChestCommand());
        manager.registerCommand(new SpeedCommand());
        manager.registerCommand(new RenameCommand());

        Bukkit.getPluginManager().registerEvents(new ChatWorker(), this);
        Bukkit.getPluginManager().registerEvents(new BanListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new CheckClaim(), this);
        Bukkit.getPluginManager().registerEvents(new ReportCommand(), this);
        Bukkit.getPluginManager().registerEvents(new HistoryCommands(), this);
        Bukkit.getPluginManager().registerEvents(new ColoredSigns(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ChatFilter(), this);
        Bukkit.getPluginManager().registerEvents(new SaveLastSurvivalLocation(), this);
        //Bukkit.getPluginManager().registerEvents(new TestStuffEvent(), this);

        MuteLoop.run();
        BanLoop.run();
        System.out.println("==============================");
        System.out.println("SabMC Plugin start-up success.");
        System.out.println("==============================");
    }

    private void sqlStuffs() throws SQLException {
        PreparedStatement s = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_reports (PLAYERUUID VARCHAR(100), MESSAGE VARCHAR(256), REPORTERUUID VARCHAR(100))");
        PreparedStatement ss = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_mutes (PLAYERUUID VARCHAR(100), REASON VARCHAR(256), DURATION INT(255), MUTERUUID VARCHAR(256))");
        PreparedStatement sss = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_bans (PLAYERUUID VARCHAR(100), REASON VARCHAR(256), DURATION INT(255), BANNERUUID VARCHAR(256))");
        PreparedStatement ssss = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_playerinfo (UUID VARCHAR(100), LASTONLINE VARCHAR(256), TELEPORTS BOOLEAN, MESSAGES BOOLEAN, CHANNEL VARCHAR(256))");
        PreparedStatement a = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_teleports (SENDERUUID VARCHAR(100), RECEIVERUUID VARCHAR(100), TYPE VARCHAR(100))");
        PreparedStatement aa = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_homes (PLAYERUUID VARCHAR(100), X DOUBLE(255,14), Y DOUBLE(255,14), Z DOUBLE(255,14), YAW FLOAT(255,13), PITCH FLOAT(255,13), WORLD VARCHAR(100), NAME VARCHAR(100))");
        PreparedStatement aaa = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_hubs (X DOUBLE(255,14), Y DOUBLE(255,14), Z DOUBLE(255,14), YAW FLOAT(255,13), PITCH FLOAT(255,13), WORLD VARCHAR(100), NAME VARCHAR(256))");
        PreparedStatement aaaa = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_requests (UUID VARCHAR(100), TYPE VARCHAR(100), WORD VARCHAR(256))");
        PreparedStatement b = sql.createStatement("CREATE TABLE IF NOT EXISTS sab_lastlocations (UUID VARCHAR(100), X DOUBLE(255,14), Y DOUBLE(255,14), Z DOUBLE(255,14), YAW FLOAT(255,13), PITCH FLOAT(255,13), WORLD VARCHAR(100))");
        s.executeUpdate();
        ss.executeUpdate();
        sss.executeUpdate();
        ssss.executeUpdate();
        a.executeUpdate();
        aa.executeUpdate();
        aaa.executeUpdate();
        aaaa.executeUpdate();
        b.executeUpdate();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static boolean inTrustedClaim(Player player) {
        boolean value = false;
        try {
            Claim claim = instance.dataStore.getClaimAt(player.getLocation(), true, null);
            if (claim == null) return false;
            value = claim.allowAccess(player) == null;
        } catch (Exception ignored) {
            System.out.println("GriefPrevention claim checking is producing errors. Is GriefPrevention installed?");
        }
        return value;
    }

    @Override
    public void onDisable() {

    }

    public static Main getPlugin() {
        return plugin;
    }

    public static Chat getChat() {
        return chat;
    }

    public static List<UUID> getSpy() {
        return spy;
    }

    public static HashMap<UUID, BukkitTask> getTimerMap() {
        return timerMap;
    }

    public static HashMap<UUID, String> getCfMap() {
        return cfMap;
    }

    public static SQL getSql() {
        return sql;
    }

    private void expiryRun(Main plugin) {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        Date newDate = new GregorianCalendar(2021, Calendar.FEBRUARY, 20).getTime();

        // todo change date
        // todo change date
        // todo change date
        // todo change date
        // todo change date
        // todo change date
        // todo change date
        // todo change date
        // todo change date


        if (date.after(newDate)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                System.out.println(Color.chat(
                "&cHi Sav, Sophia, Chloe, Kira,\n" +
                
                "&cThis plugin will never run again.\n" +
                "&cAll data will be wiped from the database.\n" +
                "&cHere is why I did this:\n\n" +
                
                "&c- Sav, gaining my trust and friendship to use me.\n" +
                "&c- Sophia, leaking our messages? Just why? What were you trying to achieve?\n" +
                "&c- Kira, calling me a paedophile for playing games and talking to Gab, for making \n" +
               "&c  sure she is okay and giving her someone to talk to about her issues.\n" +
               "&c- Disrespecting and blatantly bullying Gab.\n\n" +
                
               "&cI did have good intentions coming back into this, I did want to develop for SabMC again.\n" +
                "&cAfter I heard the things that were happening behind my back, I decided I had to\n" +
                "&cprepare for a situation like this.\n" +
                "&cI will be blocking Chloe, Sophia, Kira and Sav on all social media platforms.\n" +
                "&cIf you attempt to contact me I will be ignoring it.\n" +
                "&cI hope you all understand. \n\n" +
                
                "&cThis was my decision alone, nobody else influenced it.\n\n" +
                
                "&cBest of luck,\n" +
                "&cJack."
                ));
            }, 40);
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

}
