package me.jackfoley.sab.listeners;

import lombok.SneakyThrows;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.EnderChestFile;
import me.jackfoley.sab.worlds.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(Color.chat(Main.getPlugin().getConfig().getString("join-message").replace("{PLAYER}", e.getPlayer().getName())));
        WorldUtils.getHubLocation("hub", location -> {
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                e.getPlayer().teleport(location);
            });
        });
        PlayerInfoUtils.exists(e.getPlayer(), exists -> {
            if (!exists) {
                PlayerInfoUtils.setBasic(e.getPlayer());
            }
        });
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            EnderChestFile file = new EnderChestFile();
            List<ItemStack> currentItems = Arrays.asList(e.getPlayer().getEnderChest().getContents());
            try {
                ItemStack[] newItems = (ItemStack[]) file.getConfig().get(e.getPlayer().getUniqueId().toString() + ".contents");
                if (newItems == null) return;
                for (int i = 0; i < newItems.length; i++) {
                    if (newItems[i].equals(currentItems.get(i))) return;
                    currentItems.add(newItems[i]);
                }
            } catch (NullPointerException ignored) { }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(Color.chat(Main.getPlugin().getConfig().getString("quit-message").replace("{PLAYER}", e.getPlayer().getName())));
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            Player player = e.getPlayer();
            ItemStack[] items = player.getEnderChest().getContents();
            EnderChestFile file = new EnderChestFile();
            file.getConfig().set(player.getUniqueId().toString() + ".contents", items);
            file.saveFile();
        });
    }

}
