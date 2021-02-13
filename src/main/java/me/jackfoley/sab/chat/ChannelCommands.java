package me.jackfoley.sab.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

public class ChannelCommands extends BaseCommand {

    @CommandAlias("global|g")
    @Description("Sets the player's channel to global.")
    public void global(Player player) {
        PlayerInfoUtils.getChannel(player, channel -> {
            if (channel.equalsIgnoreCase("global")) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in &eglobal&c chat."));
                return;
            }
            PlayerInfoUtils.setChannel(player, "global");
            player.sendMessage(Color.chat(Messages.prefix + "&7You are now in &eglobal&7 chat."));
        });
    }

    @CommandAlias("staff|st|s")
    @CommandPermission("sab.staff")
    @Description("Sets the player's channel to staff chat, if they have the required permission.")
    public void staff(Player player) {
        PlayerInfoUtils.getChannel(player, channel -> {
            if (channel.equalsIgnoreCase("staff")) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in &cStaff&c chat."));
                return;
            }
            PlayerInfoUtils.setChannel(player, "staff");
            player.sendMessage(Color.chat(Messages.prefix + "&7You are now in &cStaff&7 chat."));
        });
    }

}
