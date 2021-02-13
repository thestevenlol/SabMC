package me.jackfoley.sab.worlds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

@CommandAlias("sethub")
@CommandPermission("sab.admin")
public class WorldSetCommands extends BaseCommand {

    @Subcommand("survival")
    public void setsurvival(Player player) {
        WorldUtils.setHubLocation(player.getLocation(), "survival");
        player.sendMessage(Color.chat(Messages.prefix + "&7Set the survival world spawn location to your current location."));
    }

    @Subcommand("hub")
    public void sethub(Player player) {
        WorldUtils.setHubLocation(player.getLocation(), "hub");
        player.sendMessage(Color.chat(Messages.prefix + "&7Set the hub spawn location to your current location."));
    }

}
