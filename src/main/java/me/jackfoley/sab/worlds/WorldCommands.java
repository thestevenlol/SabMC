package me.jackfoley.sab.worlds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldCommands extends BaseCommand {

    @CommandAlias("hub")
    @Description("Teleport to the server hub.")
    public void hub(Player player) {
        WorldUtils.getHubLocation("hub", hub -> {
            player.sendMessage(Color.chat(Messages.prefix + "&7Teleporting in 5 seconds..."));
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                player.teleport(hub);
            }, (20*5));
        });
    }

    @CommandAlias("survival|sur")
    @Description("Teleport to the survival world.")
    public void survival(Player player) {
        WorldUtils.getHubLocation("survival", hub -> {
            player.sendMessage(Color.chat(Messages.prefix + "&7Teleporting in 5 seconds..."));
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                player.teleport(hub);
            }, (20*5));
        });
    }

}
