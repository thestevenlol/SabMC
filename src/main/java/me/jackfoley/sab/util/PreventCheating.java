package me.jackfoley.sab.util;

import org.bukkit.entity.Player;

public class PreventCheating {

    public static boolean isFalling(Player player) {
        return player.getFallDistance() > 0;
    }

}
