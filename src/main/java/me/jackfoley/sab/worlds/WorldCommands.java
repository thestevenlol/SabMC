package me.jackfoley.sab.worlds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.LastLocationUtils;
import me.jackfoley.sab.util.Messages;
import me.jackfoley.sab.util.PreventCheating;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldCommands extends BaseCommand {

    @CommandAlias("hub")
    @Description("Teleport to the server hub.")
    public void hub(Player player) {
        WorldUtils.getHubLocation("hub", hub -> {
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                if (PreventCheating.isFalling(player)) {
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport while you are falling."));
                    return;
                }
                player.teleport(hub);
            });
        });
    }

    @CommandAlias("survival|sur")
    @Description("Teleport to the survival world.")
    public void survival(Player player) {
        WorldUtils.getHubLocation("survival", hub -> LastLocationUtils.exists(player.getUniqueId(), hasLastLocation -> LastLocationUtils.getLastLocation(player.getUniqueId(), location -> {
            if (!hasLastLocation) {
                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                    if (PreventCheating.isFalling(player)) {
                        player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport while you are falling."));
                        return;
                    }
                    player.teleport(hub);
                });
                return;
            }
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.teleport(location));
        })));
    }

}
