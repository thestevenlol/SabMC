package me.jackfoley.sab.utilcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.reporting.ReportUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadCommand extends BaseCommand {

    @CommandAlias("head")
    @CommandPermission("sab.head")
    public void head(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/head <player>"));
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            ReportUtils.getSkull(target, skull -> {
                player.sendMessage(Color.chat(Messages.prefix + "&7Retrieving head..."));
                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                   if (player.getInventory().firstEmpty() == -1) {
                       player.getWorld().dropItem(player.getLocation(), skull);
                       player.sendMessage(Color.chat(Messages.prefix + "&7Your inventory is full so we dropped the head at your feet!"));
                       return;
                   }
                   player.getInventory().addItem(skull);
                   player.sendMessage(Color.chat(Messages.prefix + "&7Added head to your inventory."));
                });
            });
        }
        if (args.length > 1) player.sendMessage(Color.chat(Messages.prefix + "&c/head <player>"));
    }

}
