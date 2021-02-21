package me.jackfoley.sab.listeners;

import me.jackfoley.sab.homes.HomeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void death(PlayerRespawnEvent e) {
        HomeUtils.getHomeLocation(e.getPlayer(), "home", location -> {
            if (location == null) return;
            e.setRespawnLocation(location);
        });
    }

}
