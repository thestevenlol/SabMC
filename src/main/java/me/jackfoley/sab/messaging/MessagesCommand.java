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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesCommand extends BaseCommand {

    @CommandAlias("message|msg|m")
    @Description("Message a player.")
    public void message(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/msg <player> <message>"));
        if (args.length == 1) player.sendMessage(Color.chat(Messages.prefix + "&c/msg " + args[0] + " <message>"));
        if (args.length > 1) {
            ModerationUtils.muted(player, muted -> {
                if (!muted) {
                    if (detectedBadWord(player, args)) {
                        return;
                    }
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
                } else {
                    ModerationUtils.getMuteDuration(player).thenAccept(duration -> ModerationUtils.getMuteReason(player).thenAccept(reason -> ModerationUtils.getMuter(player).thenAccept(muter -> {
                        if (duration == -1) {
                            player.sendMessage(Color.chat(Messages.muteMessage(muter, reason, "Permanent")));
                            return;
                        }
                        player.sendMessage(Color.chat(Messages.muteMessage(muter, reason, duration)));
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    })).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    }));
                }
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
                    if (detectedBadWord(player, args)) {
                        return;
                    }
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


    private boolean detectedBadWord(Player player, String[] words) {
        List<String> badWords = Main.getPlugin().getConfig().getStringList("bad-words");
        List<String> exclusions = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        Pattern pattern;
        Matcher matcher;
        boolean detected = false;
        for (String word : words) {
            word = word.replaceAll("1", "i");
            word = word.replaceAll("!", "i");
            word = word.replaceAll("3", "e");
            word = word.replaceAll("4", "a");
            word = word.replaceAll("5", "s");
            word = word.replaceAll("7", "t");
            word = word.replaceAll("8", "b");
            word = word.replaceAll("3", "b");
            word = word.replaceAll("0", "o");
            word = word.replaceAll("\\.", "");
            word = word.replaceAll("_", "");
            word = word.replaceAll(">", "");
            word = word.replaceAll("<", "");
            word = word.replaceAll(",", "");
            word = word.replaceAll("-", "");
            word = word.replaceAll("=", "");
            word = word.replaceAll("\\+", "");
            word = word.replaceAll("@", "a");
            word = word.replaceAll("\\$", "s");
            word = word.replaceAll("\\*", "");
            word = word.replaceAll("\\(", "");
            word = word.replaceAll("\\)", "");
            word = word.replaceAll(":", "");
            word = word.replaceAll(";", "");
            word = word.replaceAll("\\{", "");
            word = word.replaceAll("}", "");
            word = word.replaceAll("'", "");
            word = word.replaceAll("\"", "");
            word = word.replaceAll("/\\|/", "n");
            word = word.replaceAll("/", "");
            word = word.replaceAll("\\\\", "");
            word = word.replaceAll("\\[", "");
            word = word.replaceAll("]", "");
            word = word.replaceAll("\\s+", "");
            for (String badWord : badWords) {
                pattern = Pattern.compile(Pattern.quote(badWord), Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(word);
                if (matcher.find()) {
                    if (exclusions.contains(word.toLowerCase())) {
                        break;
                    }
                    detected = true;
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou are not allowed to say " + Color.strip(word) + " as it contains the blacklisted word: " + badWord));
                    break;

                }
            }
        }
        return detected;
    }

}
