package me.jackfoley.sab.listeners;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(Color.chat(Main.getPlugin().getConfig().getString("join-message").replace("{PLAYER}", e.getPlayer().getName())));
        PlayerInfoUtils.exists(e.getPlayer(), exists -> {
            if (!exists) {
                PlayerInfoUtils.setBasic(e.getPlayer());
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(Color.chat(Main.getPlugin().getConfig().getString("quit-message").replace("{PLAYER}", e.getPlayer().getName())));
    }

}
