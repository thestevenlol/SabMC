package me.jackfoley.sab.utilcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

public class SpeedCommand extends BaseCommand {

    @CommandAlias("speed")
    @CommandPermission("sab.speed")
    public void speed(Player player, String[] args) {
        if (args.length == 0) {
            if (player.isFlying()) {
                player.sendMessage(Color.chat(Messages.prefix + "&7Your current fly speed is: " + (int) (player.getFlySpeed() * 10)));
            } else {
                player.sendMessage(Color.chat(Messages.prefix + "&7Your current walk speed is: " + (int) ((player.getWalkSpeed() * 10) - 1)));
            }
        } else {
            try {
                int speed = Integer.parseInt(args[0]);
                if (speed > 10) {
                    player.sendMessage(Color.chat(Messages.prefix + "&cSpeed cannot exceed 10."));
                    return;
                }
                float newSpeed = speed / 10f;
                if (player.isFlying()) {
                    player.setFlySpeed(newSpeed);
                    player.sendMessage(Color.chat(Messages.prefix + "&7Flying speed set to: " + speed));
                    return;
                }
                player.setWalkSpeed((newSpeed == 1 ? 1 : newSpeed + 0.1f));
                player.sendMessage(Color.chat(Messages.prefix + "&7Walking speed set to: " + speed));
            } catch (NumberFormatException e) {
                player.sendMessage(Color.chat(Messages.prefix + "&cInvalid number."));
            }
        }
    }

}
