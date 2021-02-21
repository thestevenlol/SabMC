package me.jackfoley.sab.utilcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand extends BaseCommand {

    @CommandAlias("rename")
    @CommandPermission("sab.rename")
    public void rename(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/rename <new name>"));
        else {
            StringBuilder x = new StringBuilder();
            for (String arg : args) {
                x.append(arg).append(" ");
            }
            String name = x.toString();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.AIR)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot rename air you actual idiot."));
                return;
            }
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Color.chat(name));
            item.setItemMeta(meta);
            player.sendMessage(Color.chat(Messages.prefix + "&7Done."));
        }
    }

}
