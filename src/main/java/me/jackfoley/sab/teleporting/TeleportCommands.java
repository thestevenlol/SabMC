package me.jackfoley.sab.teleporting;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class TeleportCommands extends BaseCommand {

    @CommandAlias("tp|teleport")
    @CommandPermission("sab.tp")
    @CommandCompletion("@players")
    @Description("Teleport to a player")
    public void tp(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tp <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            PlayerInfoUtils.teleportsEnabled(target, enabled -> {
                if (enabled) {
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                        player.teleport(target.getLocation());
                    });
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has teleports disabled."));
            });
        }
    }

    @CommandAlias("tphere|teleporthere")
    @CommandPermission("sab.tphere")
    @CommandCompletion("@players")
    @Description("Teleport to a player")
    public void tphere(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tphere <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            PlayerInfoUtils.teleportsEnabled(target, enabled -> {
                if (enabled) {
                    Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                        target.teleport(player.getLocation());
                    });
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has teleports disabled."));
            });
        }
    }

    @CommandAlias("tpo")
    @CommandPermission("sab.tpo")
    @CommandCompletion("@players")
    @Description("Force teleport to another player.")
    public void tpo(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tpo <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            player.teleport(target.getLocation());
        }
    }

    @CommandAlias("tpohere")
    @CommandPermission("sab.tpo")
    @CommandCompletion("@players")
    @Description("Force teleport to another player.")
    public void tpohere(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tpohere <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            target.teleport(player.getLocation());
        }
    }

    @CommandAlias("tpa")
    @CommandCompletion("@players")
    @Description("Ask a player if you can teleport to them.")
    public void tpa(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tpahere <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            PlayerInfoUtils.teleportsEnabled(target, enabled -> {
                if (enabled) {

                    TeleportType type = TeleportType.TPA;
                    TeleportUtils.setTeleport(player, target, type);
                    player.sendMessage(Color.chat(Messages.prefix + "&7Request sent to " + target.getName()));
                    target.sendMessage(Color.chat(
                            "&7" + player.getName() + " would like to teleport to you.\n" +
                                    "Use /tpaccept to accept.\n" +
                                    "Use /tpdeny to deny."
                    ));
                    BukkitTask tpaTimer = Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                        player.sendMessage(Color.chat(Messages.prefix + "&7Teleport request timed out."));
                        TeleportUtils.closeTeleport(player);
                    }, 600);
                    Main.getTimerMap().put(player.getUniqueId(), tpaTimer);
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has teleports disabled."));
            });

        }
    }

    @CommandAlias("tpahere")
    @CommandCompletion("@players")
    @Description("Ask a player if they can teleport to you.")
    public void tpahere(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/tpahere <player>"));
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " is offline."));
                return;
            }

            if (target.equals(player)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot teleport to yourself."));
                return;
            }

            PlayerInfoUtils.teleportsEnabled(target, enabled -> {
                if (enabled) {

                    TeleportType type = TeleportType.TPAHERE;
                    TeleportUtils.setTeleport(player, target, type);
                    player.sendMessage(Color.chat(Messages.prefix + "&7Request sent to " + target.getName()));
                    target.sendMessage(Color.chat(
                            "&7" + player.getName() + " would like you to teleport to them.\n" +
                                    "Use /tpaccept to accept.\n" +
                                    "Use /tpdeny to deny."
                    ));
                    BukkitTask tpaHereTimer = Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                        player.sendMessage(Color.chat(Messages.prefix + "&7Teleport request timed out."));
                        TeleportUtils.closeTeleport(player);
                    }, 600);
                    Main.getTimerMap().put(player.getUniqueId(), tpaHereTimer);
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has teleports disabled."));
            });

        }
    }

    @CommandAlias("tpaccept|tpyes")
    @Description("Accept any incoming teleport requests")
    public void tpyes(Player player) {
        TeleportUtils.receiverExists(player, exists -> {
            TeleportUtils.getTeleportType(player, t -> {
                TeleportUtils.getTeleportSender(player, send -> {
                    if (exists) {
                        switch (t) {
                            case TPA:
                                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                                    OfflinePlayer sender = Bukkit.getOfflinePlayer(UUID.fromString(send));
                                    if (!sender.isOnline()) {
                                        player.sendMessage(Color.chat(Messages.prefix + "&c" + sender.getName() + " is offline. Cancelling teleport."));
                                        TeleportUtils.closeTeleport(sender);
                                        return;
                                    }
                                    sender.getPlayer().teleport(player.getLocation());
                                    TeleportUtils.closeTeleport(sender);
                                    Main.getTimerMap().get(sender.getUniqueId()).cancel();
                                    Main.getTimerMap().remove(sender.getUniqueId());
                                });
                                break;
                            case TPAHERE:
                                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                                    OfflinePlayer senders = Bukkit.getOfflinePlayer(UUID.fromString(send));
                                    if (!senders.isOnline()) {
                                        player.sendMessage(Color.chat(Messages.prefix + "&c" + senders.getName() + " is offline. Cancelling teleport."));
                                        TeleportUtils.closeTeleport(senders);
                                        return;
                                    }
                                    player.teleport(senders.getPlayer().getLocation());
                                    TeleportUtils.closeTeleport(senders);
                                    Main.getTimerMap().get(senders.getUniqueId()).cancel();
                                    Main.getTimerMap().remove(senders.getUniqueId());
                                });
                                break;
                        }
                        return;
                    }
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou have no incoming requests"));
                });
            });
        });
    }

    @CommandAlias("tpdeny|tpno")
    @Description("Deny a teleport request.")
    public void deny(Player player) {
        TeleportUtils.receiverExists(player, receiverExists -> {
            TeleportUtils.getTeleportType(player, type -> {
                TeleportUtils.getTeleportSender(player, sender -> {
                    if (receiverExists) {
                        TeleportUtils.closeTeleport(Bukkit.getPlayer(UUID.fromString(sender)));
                        player.sendMessage(Color.chat(Messages.prefix + "&cTeleport request denied."));
                        Bukkit.getPlayer(UUID.fromString(sender)).sendMessage(Color.chat(Messages.prefix + "&c" + player.getName() + " denied your teleport request."));
                    }
                });
            });
        });
    }

}
