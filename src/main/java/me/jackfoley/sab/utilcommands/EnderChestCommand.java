package me.jackfoley.sab.utilcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.EnderChestFile;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestCommand extends BaseCommand {

    @CommandAlias("echest|enderchest")
    @CommandPermission("sab.echest")
    public void echest(Player player, String[] args) {
        Inventory enderChest;
        if (args.length == 0) {
            enderChest = player.getEnderChest();
            player.openInventory(enderChest);
        } else if (args.length == 1 && player.hasPermission("sab.echest.others")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " has never played on the server before."));
                return;
            }
            if (target.isOnline()) {
                player.openInventory(target.getPlayer().getEnderChest());
                return;
            }
            Inventory inventory = Bukkit.createInventory(null, 27, target.getName() +"'s Enderchest");
            EnderChestFile file = new EnderChestFile();
            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                ItemStack[] items = (ItemStack[]) file.getConfig().get(target.getUniqueId().toString() + ".contents");
                if (items == null) {
                    player.openInventory(inventory);
                    return;
                }
                for (int i = 0; i < items.length; i++) {
                    inventory.setItem(i, items[i]);
                }
            });
            player.openInventory(inventory);
        }

    }

}
