package me.jackfoley.sab.utilcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

public class FlyCommand extends BaseCommand {

    @CommandAlias("fly")
    @CommandPermission("sab.fly")
    @Description("Make yourself fly")
    public void fly(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(Color.chat(Messages.prefix + "&7Flying has been disabled."));
            return;
        }

        if (Main.inTrustedClaim(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(Color.chat(Messages.prefix + "&7Flying has been enabled."));
        } else {
            player.sendMessage(Color.chat(Messages.prefix + "&cYou are not in a trusted claim!"));
        }

    }

}
