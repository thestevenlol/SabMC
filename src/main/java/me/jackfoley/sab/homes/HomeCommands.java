package me.jackfoley.sab.homes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class HomeCommands extends BaseCommand {

    private int getMaxHomes(OfflinePlayer player) {
        int value = 0;
        Set<PermissionAttachmentInfo> permInfo = player.getPlayer().getEffectivePermissions();
        for (PermissionAttachmentInfo perm : permInfo) {
            if (perm.getPermission().startsWith("sab.homes.")) {
                value = Integer.parseInt(perm.getPermission().substring(10));
                break;
            }
        }
        return value;
    }


    @CommandAlias("sethome")
    public void sethome(Player player, String[] args) {
        final int max = getMaxHomes(player);
        HomeUtils.getTotalHomes(player, total -> {
            if (total < max) {
                if (args.length == 0) {
                    HomeUtils.getHomes(player, homes -> {
                        HomeUtils.certainHomeExists(player, "home", exists -> {
                            if (exists) {
                                player.sendMessage(Color.chat(Messages.prefix + "&cYou already have a home called \"home.\""));
                                return;
                            }
                            HomeUtils.setHome(player, "home");
                            final int remaining = max - (total + 1);
                            player.sendMessage(Color.chat(Messages.prefix + "&7Set a new home called \"home\" to your current location." +
                                    "\nYou have " + remaining + (remaining == 1 ? " more home available to be set." : " more homes available to be set.")));
                        });
                    });
                } else if (args.length == 1) {
                    HomeUtils.getHomes(player, homes -> {
                        HomeUtils.certainHomeExists(player, args[0], exists -> {
                            if (exists) {
                                player.sendMessage(Color.chat(Messages.prefix + "&cYou already have a home called \"" + args[0] + ".\""));
                                return;
                            }
                            HomeUtils.setHome(player, args[0]);
                            final int remaining = max - (total + 1);
                            player.sendMessage(Color.chat(Messages.prefix + "&7Set a new home called \"" + args[0] + "\" to your current location." +
                                    "\nYou have " + remaining + (remaining == 1 ? " more home available to be set." : " more homes available to be set.")));
                        });
                    });
                }
            } else {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou cannot set any more homes. You have reached your maximum amount of homes. (" + total + "/" + max + ")"));
            }
        });
    }

    @CommandAlias("home")
    @CommandCompletion("@players")
    public void home(Player player, String[] args) {
        if (args.length == 0) {
            HomeUtils.hasAnyHomes(player, value -> {
                HomeUtils.getHomes(player, homes -> {
                    if (value) {
                        if (homes.stream().anyMatch(h -> h.equalsIgnoreCase("home"))) { // has a home with the name they entered
                            HomeUtils.getHomeLocation(player, "home", location -> {
                                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.teleport(location));
                            });
                        } else {
                            player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have a home called \"home\"."));
                        }
                        return;
                    }
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have any homes set. Set a home with /sethome <homeName>"));
                });
            });
        } else if (args.length == 1) {
            HomeUtils.hasAnyHomes(player, value -> {
                HomeUtils.getHomes(player, homes -> {
                    if (value) {
                        if (homes.stream().anyMatch(h -> h.equalsIgnoreCase(args[0]))) { // has a home with the name they entered
                            HomeUtils.getHomeLocation(player, args[0], location -> {
                                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> player.teleport(location));
                            });
                        } else {
                            player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have a home called \"" + args[0] + "\"."));
                        }
                        return;
                    }
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have any homes set. Set a home with /sethome <homeName>"));
                });
            });
        } else if (args.length == 2) {
            if (player.hasPermission("sab.homes.other")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                String home = args[1];
                if (!target.hasPlayedBefore()) {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has never played on the server before."));
                    return;
                }
                HomeUtils.certainHomeExists(target, home, exists -> {
                    if (exists) {
                      HomeUtils.getHomeLocation(target, home, location -> {
                          Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                             player.teleport(location);
                          });
                      });
                      return;
                    }
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " doesn't have a home called " + home + "."));
                });
            } else {
                player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have permission to use this."));
            }
        } else {
            player.sendMessage(Color.chat(Messages.prefix + "&c/home [homeName]"));
        }
    }

    @CommandAlias("homes")
    @Description("Lists your set homes.")
    public void homes(Player player, String[] args) {
        if (args.length == 0) {
            HomeUtils.hasAnyHomes(player, hasHomes -> HomeUtils.getHomes(player, homes -> {
                if (hasHomes) {
                    player.sendMessage(Color.chat("&8&m====&8[&5 Homes&8 ]&8&m===="));
                    homes.forEach(h -> player.sendMessage(Color.chat("&8- &d") + h));
                    player.sendMessage(Color.chat("&8&m====&8[&5 Homes&8 ]&8&m===="));
                } else {
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou do not have any homes set."));
                }
            }));
        } else if (args.length == 1 && player.hasPermission("sab.homes.other")) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore()) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + targetPlayer.getName() + " has never played on the server before."));
                return;
            }
            HomeUtils.getHomes(targetPlayer, homes -> HomeUtils.hasAnyHomes(targetPlayer, hasHomes -> {
                if (hasHomes) {
                    player.sendMessage(Color.chat("&8&m====&8[&5 Homes&8 ]&8&m===="));
                    homes.forEach(h -> player.sendMessage(Color.chat("&8- &d") + h));
                    player.sendMessage(Color.chat("&8&m====&8[&5 Homes&8 ]&8&m===="));
                } else {
                    player.sendMessage(Color.chat(Messages.prefix + "&c" + targetPlayer.getName() + " does not have any homes set."));
                }
            }));
        }
    }

    @CommandAlias("delhome")
    public void delhome(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Color.chat(Messages.prefix + "&c/delhome <home>"));
        } else if (args.length == 1) {
            String home = args[0];
            HomeUtils.certainHomeExists(player, home, exists -> {
                if (!exists) {
                    player.sendMessage(Color.chat(Messages.prefix + "&cThe home \"" + home + "\" does not exist."));
                    return;
                }
                HomeUtils.deleteHome(home, player);
                player.sendMessage(Color.chat(Messages.prefix + "&7Home deleted."));
            });
        } else {
            player.sendMessage(Color.chat(Messages.prefix + "&c/delhome <home>"));
        }
    }

    @HelpCommand
    @CatchUnknown
    @Default
    public void homeHelp(Player player) {
        if (player.hasPermission("sab.homes.other")) {
            player.sendMessage(Color.chat("&5Sab&dMC &5home commands:"));
            player.sendMessage(Color.chat("&5/home <home>"));
            player.sendMessage(Color.chat("&5/home <player> <home>"));
            player.sendMessage(Color.chat("&5/sethome <home>"));
            player.sendMessage(Color.chat("&5/homes"));
            return;
        }
        player.sendMessage(Color.chat("&5Sab&dMC &5home commands:"));
        player.sendMessage(Color.chat("&5/home <home>"));
        player.sendMessage(Color.chat("&5/sethome <home>"));
        player.sendMessage(Color.chat("&5/homes"));
    }

}
