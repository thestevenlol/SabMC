package me.jackfoley.sab.util;

import org.bukkit.ChatColor;

public class Color {
    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String strip(String message) {
        return ChatColor.stripColor(message);
    }

}
