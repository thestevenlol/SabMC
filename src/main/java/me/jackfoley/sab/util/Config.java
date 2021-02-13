package me.jackfoley.sab.util;

import me.jackfoley.sab.Main;

public class Config {

    public Config(Main plugin) {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

}
