package me.jackfoley.sab.listeners;

import me.jackfoley.sab.util.LastLocationUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class SaveLastSurvivalLocation implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        System.out.println(x + " " + y + " " + z + " " + yaw + " " + pitch + " " + player.getWorld().getName());
        LastLocationUtils.exists(uuid, exists -> {
            if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("hub")) {
                if (!exists) {
                    LastLocationUtils.insert(uuid, x, y, z, yaw, pitch, player.getWorld().getName());
                    return;
                }
                LastLocationUtils.update(uuid, x, y, z, yaw, pitch, player.getWorld().getName());
            }
        });
    }

    @EventHandler
    public void worldChanged(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        UUID uuid = e.getPlayer().getUniqueId();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        System.out.println(x + " " + y + " " + z + " " + yaw + " " + pitch + " " + player.getWorld().getName());
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("hub")) {
            LastLocationUtils.exists(uuid, exists -> {
                if (!exists) {
                    LastLocationUtils.insert(uuid, x, y, z, yaw, pitch, e.getFrom().getWorld().getName());
                    return;
                }
                LastLocationUtils.update(uuid, x, y, z, yaw, pitch, e.getFrom().getWorld().getName());
            });
        }
    }

}
