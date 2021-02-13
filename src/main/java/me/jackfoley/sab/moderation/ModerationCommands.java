package me.jackfoley.sab.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.messaging.MessageManager;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Formatting;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.UUID;

import static me.jackfoley.sab.moderation.ModerationUtils.handleMute;

public class ModerationCommands extends BaseCommand {

    @CommandAlias("mute")
    @CommandPermission("sab.mute")
    @Description("Mute a player.")
    @CommandCompletion("@players")
    public void mute(Player player, String[] args) throws SQLException {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/mute <player> <reason>"));
        if (args.length == 1) player.sendMessage(Color.chat(Messages.prefix + "&c/mute " + args[0] + " <reason>"));
        if (args.length > 1) {
            StringBuilder x = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            String reason = x.toString().trim();
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou can not mute yourself."));
                return;
            }

            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + Messages.neverPlayedBeforeError));
                return;
            }

            ModerationUtils.muted(target, muted -> {
                if (muted) {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is already muted."));
                    return;
                }

                ModerationUtils.totalMutes(target).thenAccept(total -> {
                    System.out.println(total);
                    switch (total) {
                        case 0:
                            handleMute(Main.getSql(), target, reason, 300, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 300)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 1:
                            handleMute(Main.getSql(), target, reason, 600, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 600)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 2:
                            handleMute(Main.getSql(), target, reason, 1200, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 1200)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 3:
                            handleMute(Main.getSql(), target, reason, 1800, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 1800)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 4:
                            handleMute(Main.getSql(), target, reason, 3600, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 3600)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 5:
                            handleMute(Main.getSql(), target, reason, 21600, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 21600)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 6:
                            handleMute(Main.getSql(), target, reason, 604800, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, 604800)));
                            muteBroadcast(player, target, reason);
                            break;
                        case 7:
                            handleMute(Main.getSql(), target, reason, -1, player);
                            if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.muteMessage(player, reason, "Permanent")));
                            muteBroadcast(player, target, reason);
                            break;
                    }
                }).exceptionally(throwable ->  {
                    throwable.printStackTrace();
                    return null;
                });
            });
        }
    }

    private void muteBroadcast(Player player, OfflinePlayer target, String reason) {
        Bukkit.broadcastMessage(Color.chat("&c" + target.getName() + "&7 has been muted by&c " + player.getName() + "&7:&c " + reason));
    }

    @CommandAlias("unmute")
    @CommandPermission("sab.unmute")
    @Description("Unmute a player who is muted.")
    @CommandCompletion("@players")
    public void unmute(Player player, String[] args) throws SQLException {
        if (args.length == 0) {
            player.sendMessage(Color.chat(Messages.prefix + "&c/unmute <player>"));
        } else if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + Messages.neverPlayedBeforeError));
                return;
            }

            ModerationUtils.muted(target, muted -> {
                if (muted) {
                    player.sendMessage(Color.chat(Messages.prefix + "&7" + target.getName() + Messages.unmuteMessage));
                    ModerationUtils.unmute(Main.getSql(), target);
                    if (target.isOnline()) target.getPlayer().sendMessage(Color.chat(Messages.prefix + Messages.targetUnmuteMessage + player.getName() + "."));
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is not muted."));
            });
        } else {
            player.sendMessage(Color.chat(Messages.prefix + "&c/unmute <player>"));
        }
    }

    @CommandAlias("ban")
    @CommandPermission("sab.ban")
    @Description("Ban a player.")
    @CommandCompletion("@players")
    public void ban(Player player, String[] args) throws SQLException {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/ban <player> <reason>"));
        if (args.length == 1) player.sendMessage(Color.chat(Messages.prefix + "&c/ban " + args[0] + " <reason>"));
        if (args.length > 1) {
            StringBuilder x = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            String reason = x.toString().trim();
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + Messages.neverPlayedBeforeError));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou can not ban yourself."));
                return;
            }

            ModerationUtils.banned(target).thenAccept(banned -> {
                if (banned) {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + Messages.alreadyBannedError));
                    return;
                }
                ModerationUtils.ban(target, reason, player);
                player.sendMessage(Color.chat(Messages.prefix + "&7Banned " + target.getName() + "."));
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }
    }

    @CommandAlias("unban")
    @CommandPermission("sab.unban")
    @CommandCompletion("@players")
    @Description("Unban a player who is banned.")
    public void unban(Player player, String[] args) throws SQLException {
        if (args.length == 0) {
            player.sendMessage(Color.chat(Messages.prefix + "&c/unban <player>"));
        } else if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + Messages.neverPlayedBeforeError));
                return;
            }

            ModerationUtils.banned(target).thenAccept(banned -> {
                if (!banned) {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is not banned."));
                    return;
                }
                ModerationUtils.unban(target);
                player.sendMessage(Color.chat(Messages.prefix + "&7Unbanned " + target.getName()));
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });

        } else {
            player.sendMessage(Color.chat(Messages.prefix + "&c/unban <player>"));
        }

    }

    @CommandAlias("tempban")
    @CommandCompletion("@players")
    @CommandPermission("sab.tempban")
    @Description("Temporarily ban a player.")
    public void tempban(Player player, String[] args) throws SQLException {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tempban <player> <duration> <reason>"));
        if (args.length == 1) player.sendMessage(Color.chat(Messages.prefix + "&c/tempban " + args[0] + " <duration> <reason>"));
        if (args.length == 2) player.sendMessage(Color.chat(Messages.prefix + "&c/tempban " + args[0] + " " + args[1] + " <reason>"));
        if (args.length > 2) {
            String durationString = args[1];
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + Messages.neverPlayedBeforeError));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou can not ban yourself."));
                return;
            }

            int duration;
            try {
                duration = Formatting.getBanTime(durationString);
            } catch (NumberFormatException e) {
                player.sendMessage(Color.chat(Messages.prefix + "&c/tempban <player> <duration> <reason>"));
                return;
            }
            StringBuilder x = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            String reason = x.toString().trim();

            int finalDuration = duration;
            ModerationUtils.banned(target).thenAccept(banned -> {
                if (banned) {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + Messages.alreadyBannedError));
                    return;
                }
                ModerationUtils.tempban(target, reason, player, finalDuration); // target reason sender duration
            }).exceptionally(throwable -> {
               throwable.printStackTrace();
               return null;
            });
            player.sendMessage(Color.chat(Messages.prefix + "&7Temporarily banned " + target.getName() + " for " + Formatting.time(duration) + "."));
        }
    }

    @CommandAlias("kick")
    @CommandPermission("sab.kick")
    @CommandCompletion("@players")
    @Description("Kick a player from the server.")
    public void kick(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat("&c/kick <player> [reason]"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " is not online."));
                return;
            }
            ModerationUtils.kick(target, player, "None specified.");
        } else {
            StringBuilder x = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat("&c" + args[0] + " is not online."));
                return;
            }
            ModerationUtils.kick(target, player, x.toString().trim());
        }
    }


    @CommandAlias("spy")
    @CommandPermission("sab.spy")
    public void spy(Player player) {
        if (Main.getSpy().contains(player.getUniqueId())) {
            Main.getSpy().remove(player.getUniqueId());
            player.sendMessage(Color.chat(Messages.prefix + "&7You are no longer in social-spy."));
            return;
        }
        Main.getSpy().add(player.getUniqueId());
        player.sendMessage(Color.chat(Messages.prefix + "&7You are now in social-spy."));
    }

    @CommandAlias("seenip")
    @CommandPermission("sab.seenip")
    public void seenip(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/seenip <numeric_ip>"));

        if (args.length == 1) {
            ModerationUtils.getPlayersViaIP(args[0], list -> {
                player.sendMessage(Color.chat("&8&m==&8[&5 Accounts linked to &d" + args[0] + "&8 ]&8&m=="));
                list.forEach(i -> player.sendMessage(Color.chat("&8- &b") + Bukkit.getOfflinePlayer(UUID.fromString(i)).getName()));
                player.sendMessage(Color.chat("&8&m==&8[&5 Accounts linked to &d" + args[0] + "&8 ]&8&m=="));
            });
        }

        if (args.length > 1) player.sendMessage(Color.chat(Messages.prefix + "&c/seenip <numeric_ip>"));
    }

    // why is this in moderation.... idk.. fuck it
    @CommandAlias("seen")
    @CommandCompletion("@players")
    public void seen(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/seen <player>"));

        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has never played on the server before."));
                return;
            }
            /*

            format:
            Username: x
            Nickname: (if set) x
            Muted: x
            Banned: x

            IP: (x)xx.xxx.xxx.xxx

             */

            String format = "&5Username:&d {USERNAME}\n" +
                    "&5Muted:&d {MUTED}\n" +
                    "&5Banned:&d {BANNED}\n";

            //"\n\n&5IP:&d " + player.getPlayer().getAddress().getAddress().getHostAddress()
            String formatWithIP = "&5Username:&d {USERNAME}\n" +
                    "&5Muted:&d {MUTED}\n" +
                    "&5Banned:&d {BANNED}\n\n" +
                    "&5IP:&d " + target.getPlayer().getAddress().getAddress().getHostAddress();

            ModerationUtils.banned(target).thenAccept(banned -> {
                ModerationUtils.muted(target, muted -> {
                    if (player.hasPermission("sab.seen.ip")) {
                        player.sendMessage(Color.chat(formatWithIP
                                .replace("{USERNAME}", target.getName())
                                .replace("{MUTED}", (muted ? "Yes" : "No"))
                                .replace("{BANNED}", (banned ? "Yes" : "No"))));
                        return;
                    }
                    player.sendMessage(Color.chat(format
                            .replace("{USERNAME}", target.getName())
                            .replace("{MUTED}", (muted ? "Yes" : "No"))
                            .replace("{BANNED}", (banned ? "Yes" : "No"))));
                });
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });

        }

        if (args.length > 1) player.sendMessage(Color.chat(Messages.prefix + "&c/seen <player>"));
    }

    @CommandAlias("invsee")
    @CommandPermission("sab.invsee")
    public void invsee(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/invsee <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }
            Inventory inventory = target.getInventory();
            player.openInventory(inventory);
        }
        if (args.length > 1) player.sendMessage(Color.chat(Messages.prefix + "&c/invsee <player>"));
    }

}
