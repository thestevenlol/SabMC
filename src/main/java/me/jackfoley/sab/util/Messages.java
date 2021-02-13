package me.jackfoley.sab.util;

import me.jackfoley.sab.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Messages {

    public static String prefix = Main.getPlugin().getConfig().getString("prefix");
    public static String neverPlayedBeforeError = " has never played on the server before.";
    public static String unmuteMessage = " has been un-muted.";
    public static String targetUnmuteMessage = "&7You have been unmuted by ";
    public static String alreadyBannedError = " is already banned.";

    public static String muteMessage(String muter, String reason, int duration) {
        return "&8&m==========&8[&c&l MUTED&8 ]&8&m==========\n" +
                "&7You have been muted by:&c " + Bukkit.getOfflinePlayer(UUID.fromString(muter)).getName() + "\n" +
                "&7Reason:&c " + reason + "\n" +
                "&7Time remaining:&c " + Formatting.time(duration) + "\n" +
                "&8&m==========&8[&c&l MUTED&8 ]&8&m==========";
    }

    public static String muteMessage(String muter, String reason, String duration) {
        return "&8&m==========&8[&c&l MUTED&8 ]&8&m==========\n" +
                "&7You have been muted by:&c " + Bukkit.getOfflinePlayer(UUID.fromString(muter)).getName() + "\n" +
                "&7Reason:&c " + reason + "\n" +
                "&7Time remaining:&c " + duration + "\n" +
                "&8&m==========&8[&c&l MUTED&8 ]&8&m==========";
    }

    public static String muteMessage(Player player, String reason, int duration) {
        return "&8&m==========&8[&c&l MUTED&8 ]&8&m==========\n" +
                "&7You have been muted by:&c " + player.getName() + "\n" +
                "&7Reason:&c " + reason + "\n" +
                "&7Time remaining:&c " + Formatting.time(duration) + "\n" +
                "&8&m==========&8[&c&l MUTED&8 ]&8&m==========";
    }

    public static String muteMessage(Player player, String reason, String duration) {
        return "&8&m==========&8[&c&l MUTED&8 ]&8&m==========\n" +
                "&7You have been muted by:&c " + player.getName() + "\n" +
                "&7Reason:&c " + reason + "\n" +
                "&7Time remaining:&c " + duration + "\n" +
                "&8&m==========&8[&c&l MUTED&8 ]&8&m==========";
    }

    public static String banMessage(OfflinePlayer player, String reason) {
        return "&cYou have been permanently banned.\n" +
                "\n" +
                "Reason:&7 " + reason + "\n" +
                "&cBanned by: &7" + player.getName();
    }

    public static String tempBanMessage(OfflinePlayer player, String reason, String duration) {
        return "&cYou have been banned.\n" +
                "\n" +
                "Reason:&7 " + reason + "\n" +
                "&cBanned by: &7" + player.getName() +
                "\n&cTime remaining: &7" + duration;
    }

    public static String kickMessage(Player kicker, String reason) {
        return "&cYou have been kicked from the server\n" +
                " \n" +
                "&cReason:&7 " + reason + "\n" +
                "&cKicked by:&7 " + kicker.getName();
    }
}
