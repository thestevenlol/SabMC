package me.jackfoley.sab.messaging;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import me.jackfoley.sab.Main;
import me.jackfoley.sab.moderation.ModerationUtils;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessagesCommand extends BaseCommand {

    @CommandAlias("message|msg|m")
    @Description("Message a player.")
    public void message(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/msg <player> <message>"));
        if (args.length == 1) player.sendMessage(Color.chat(Messages.prefix + "&c/msg " + args[0] + " <message>"));
        if (args.length > 1) {
            StringBuilder x = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                x.append(args[i]).append(" ");
            }
            String msg = x.toString();
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Color.chat(Messages.prefix + "&c" + args[0] + " is offline."));
                return;
            }
            PlayerInfoUtils.messagesEnabled(target, enabled -> {
                if (enabled) {
                    player.sendMessage(Color.chat("&bTo " + target.getName() + ":&f ") + msg);
                    target.sendMessage(Color.chat("&bFrom " + player.getName() + ":&f ") + msg);
                    MessageManager.setReplyTarget(player, target);
                    Bukkit.getOnlinePlayers().forEach(online -> {
                        if (online.hasPermission("sab.spy") && Main.getSpy().contains(online.getUniqueId())) {
                            online.sendMessage(Color.chat("&c[SPY]&e " + player.getName() + " -> " + target.getName() + ": " + msg));
                        }
                    });
                    return;
                }
                player.sendMessage(Color.chat(Messages.prefix + "&c" + target.getName() + " has messaging disabled."));
            });
        }
    }

    @CommandAlias("reply|r")
    @Description("Reply to a player who messaged you.")
    public void reply(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/r <message>"));
        if (args.length > 0) {
            ModerationUtils.muted(player, muted -> {
                if (!muted) {
                    StringBuilder x = new StringBuilder();
                    for (String arg : args) {
                        x.append(arg).append(" ");
                    }
                    String msg = x.toString();
                    Player target = MessageManager.getReplyTarget(player);
                    if (target == null) {
                        player.sendMessage(Color.chat(Messages.prefix + "&cYou have nobody to reply to. Maybe they went offline?"));
                        return;
                    }
                    player.sendMessage(Color.chat("&bTo " + target.getName() + ":&f ") + msg);
                    target.sendMessage(Color.chat("&bFrom " + player.getName() + ":&f ") + msg);
                    MessageManager.setReplyTarget(player, target);
                    Bukkit.getOnlinePlayers().forEach(online -> {
                        if (online.hasPermission("sab.spy") && Main.getSpy().contains(online.getUniqueId())) {
                            online.sendMessage(Color.chat("&c[SPY]&e " + player.getName() + " -> " + target.getName() + ": " + msg));
                        }
                    });
                } else {
                    ModerationUtils.getMuteDuration(player).thenAccept(duration -> {
                        ModerationUtils.getMuteReason(player).thenAccept(reason -> {
                            ModerationUtils.getMuter(player).thenAccept(muter -> {
                                if (duration == -1) {
                                    player.sendMessage(Color.chat(Messages.muteMessage(muter, reason, "Permanent")));
                                    return;
                                }
                                player.sendMessage(Color.chat(Messages.muteMessage(muter, reason, duration)));
                            }).exceptionally(throwable -> {
                                throwable.printStackTrace();
                                return null;
                            });
                        }).exceptionally(throwable -> {
                            throwable.printStackTrace();
                            return null;
                        });
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
                }
            });
        }
    }

}
