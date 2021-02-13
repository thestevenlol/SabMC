package me.jackfoley.sab.history;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class HistoryCommands extends BaseCommand implements Listener {

    public static UUID id;

    @CommandAlias("history")
    @CommandCompletion("@players")
    public void history(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/history <player>"));
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has never played on the server before."));
                return;
            }
            id = target.getUniqueId();
            Inventory inventory = Bukkit.createInventory(null, 9, Color.chat("&cPunishment History: " + target.getName()));
            ItemStack mutes = new ItemStack(Material.FEATHER);
            ItemStack bans = new ItemStack(Material.NETHERITE_AXE);
            ItemMeta mutesM = mutes.getItemMeta();
            ItemMeta bansM = bans.getItemMeta();
            mutesM.setDisplayName(Color.chat("&dMute History"));
            bansM.setDisplayName(Color.chat("&dBan History"));
            mutes.setItemMeta(mutesM);
            bans.setItemMeta(bansM);
            inventory.setItem(3, mutes);
            inventory.setItem(5, bans);
            player.openInventory(inventory);
        }
    }

    @EventHandler
    public void onSelectionBan(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith(Color.chat("&cBan History:"))) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSelectionMute(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith(Color.chat("&cMute History:"))) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onSelection(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith(Color.chat("&cPunishment History:"))) return;
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR)) return;
        switch (e.getCurrentItem().getType()) {
            case FEATHER:
                player.closeInventory();
                player.openInventory(HistoryUtils.createMuteHistoryInventory(id));
                break;
            case NETHERITE_AXE:
                player.closeInventory();
                player.openInventory(HistoryUtils.createBanHistoryInventory(id));
                break;
        }
    }

}
