package me.jackfoley.sab.gamemode;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommands extends BaseCommand {

    @CommandAlias("gmc")
    @CommandPermission("sab.gmc")
    public void gmc(Player player, String[] args) {
        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in creative mode."));
            else player.setGameMode(GameMode.CREATIVE); player.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }
            if (target.getGameMode().equals(GameMode.CREATIVE)) player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is already in creative mode."));
            else {
                target.setGameMode(GameMode.CREATIVE);
                target.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
                player.sendMessage(Color.chat(Messages.prefix + "&7Set " + target.getName() + "'s gamemode to creative."));
            }
        }
    }

    @CommandAlias("gms")
    @CommandPermission("sab.gms")
    public void gms(Player player, String[] args) {
        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.SURVIVAL)) player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in survival mode."));
            else player.setGameMode(GameMode.SURVIVAL); player.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }
            if (target.getGameMode().equals(GameMode.SURVIVAL)) player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is already in survival mode."));
            else {
                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
                player.sendMessage(Color.chat(Messages.prefix + "&7Set " + target.getName() + "'s gamemode to survival."));
            }
        }
    }

    @CommandAlias("gma")
    @CommandPermission("sab.gma")
    public void gma(Player player, String[] args) {
        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in adventure mode."));
            else player.setGameMode(GameMode.ADVENTURE); player.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }
            if (target.getGameMode().equals(GameMode.ADVENTURE)) player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is already in adventure mode."));
            else {
                target.setGameMode(GameMode.ADVENTURE);
                target.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
                player.sendMessage(Color.chat(Messages.prefix + "&7Set " + target.getName() + "'s gamemode to adventure."));
            }
        }
    }

    @CommandAlias("gmsp")
    @CommandPermission("sab.gmsp")
    public void gmsp(Player player, String[] args) {
        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) player.sendMessage(Color.chat(Messages.prefix + "&cYou are already in spectator mode."));
            else player.setGameMode(GameMode.SPECTATOR); player.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }
            if (target.getGameMode().equals(GameMode.SPECTATOR)) player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is already in spectator mode."));
            else {
                target.setGameMode(GameMode.SPECTATOR);
                target.sendMessage(Color.chat(Messages.prefix + "&7You gamemode has been updated."));
                player.sendMessage(Color.chat(Messages.prefix + "&7Set " + target.getName() + "'s gamemode to spectator."));
            }
        }
    }

}
