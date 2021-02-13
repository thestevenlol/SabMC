package me.jackfoley.sab.reporting;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportCommand extends BaseCommand implements Listener {

    public static UUID reportedUUID = null;
    public static UUID reporterUUID = null;
    public static String msg = "";

    @CommandAlias("report")
    public void report(Player player, String[] args) {

        // /report uuid message

        if (args.length < 2) {
            player.sendMessage(Color.chat(Messages.prefix + "&cUsage blocked."));
            return;
        }

        try {
            System.out.println(args[0]);
            reportedUUID = UUID.fromString(args[0]);
            reporterUUID = player.getUniqueId();
            StringBuilder x = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            String message = x.toString();
            msg = message;
            Inventory inv = ReportUtils.createSendReportConfirmInventory();

            ReportUtils.reportAlreadyExists(reportedUUID, message, exists -> {
                if (!exists) {
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.openInventory(inv));
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&cThis message has already been reported."));
            });
        } catch (Exception e) {
            player.sendMessage(Color.chat(Messages.prefix + "&cInvalid."));
        }

    }

    @EventHandler
    public void onReportInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Color.chat("&cReport"))) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR)) return;
        switch (e.getCurrentItem().getType()) {
            case LIME_WOOL:
                e.getWhoClicked().closeInventory();
                ReportUtils.setReport(e.getWhoClicked().getUniqueId(), reportedUUID, msg);
                e.getWhoClicked().sendMessage(Color.chat(Messages.prefix + "&7Message reported successfully. Thank you!"));
                break;
            case RED_WOOL:
                e.getWhoClicked().closeInventory();
                break;
        }
    }

    @CommandAlias("reports")
    @CommandPermission("sab.reports")
    public void reports(Player player) {
        Inventory inv = ReportUtils.createReportsInventory();
        player.openInventory(inv);
    }

    @EventHandler
    public void onReportsInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Color.chat("&cReports"))) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR)) return;
        if (e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            e.getWhoClicked().closeInventory();
            Inventory inv = ReportUtils.createDoReportConfirmInventory();
            e.getWhoClicked().openInventory(inv);
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            reportedUUID = UUID.fromString(meta.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "uuid"), PersistentDataType.STRING));
            reporterUUID = UUID.fromString(meta.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "reporteruuid"), PersistentDataType.STRING));
            msg = meta.getPersistentDataContainer().get(new NamespacedKey(Main.getPlugin(), "message"), PersistentDataType.STRING);
        }
    }

    @EventHandler
    public void onReportsConfirmInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(Color.chat("&cConfirm report?"))) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR)) return;
        switch (e.getCurrentItem().getType()) {
            case LIME_WOOL:
                e.getWhoClicked().closeInventory();
                ReportUtils.doReport((Player) e.getWhoClicked(), Bukkit.getOfflinePlayer(reportedUUID), Bukkit.getOfflinePlayer(reporterUUID));
                break;
            case RED_WOOL:
                e.getWhoClicked().closeInventory();
                ReportUtils.deleteSingle(reportedUUID, reporterUUID, msg);
                break;
        }
    }

}
