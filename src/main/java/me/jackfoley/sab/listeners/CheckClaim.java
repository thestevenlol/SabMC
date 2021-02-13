package me.jackfoley.sab.listeners;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CheckClaim implements Listener {

    @EventHandler
    public void onClaimLeave(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (p.hasPermission("polo.fly.bypass")) return;
        if (p.getWorld().getName().equalsIgnoreCase("hub")) return;
        if (!Main.inTrustedClaim(e.getPlayer()) && e.getPlayer().getAllowFlight()) {

            if (e.getTo() == null) return;
            if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY()) {
                return;
            }

            p.setAllowFlight(false);
            p.setFlying(false);
            p.setFallDistance(0 - p.getLocation().getBlockY());

            e.getPlayer().sendMessage(Color.chat(Messages.prefix + "&cYou are not in a claim you own or are trusted in, flying has been disabled."));
        }
    }

}
