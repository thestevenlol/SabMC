package me.jackfoley.sab.chat;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.moderation.ModerationUtils;
import me.jackfoley.sab.playerinfo.PlayerInfoUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatWorker implements Listener {

    private String getPrefixes(Player player) {
        Chat chat = Main.getChat();
        return chat.getPlayerPrefix(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        e.setCancelled(true);

        ModerationUtils.muted(player, muted -> {
           if (!muted) {
               if (detectedBadWord(player, e.getMessage().split("\\s+"))) {
                   return;
               }
               PlayerInfoUtils.getChannel(player, c -> {
                   PlayerInfoUtils.getChannelPlayers(c, players -> {
                       if (c.equalsIgnoreCase("global")) {
                           for (UUID uuid : players) {

                               Player channelPlayer = Bukkit.getPlayer(uuid);

                               TextComponent channel = new TextComponent(Color.chat("&7[&eG&7] "));
                               try {
                                   if (channelPlayer.hasPermission("sab.staff")) {
                                       channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.chat("&aAvailable channels:" +
                                               "\n&eGlobal &8- &7Current" +
                                               "\n&cStaff")).create()));
                                   } else {
                                       channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.chat("&aAvailable channels:" +
                                               "\n&eGlobal &8- &7Current" +
                                               "\n&dAdvertising")).create()));
                                   }
                               } catch (NullPointerException ignored) {}

                               TextComponent username = new TextComponent(Color.chat(getPrefixes(player) + player.getDisplayName()));
                               TextComponent barrier2 = new TextComponent(Color.chat("&e >&f "));
                               TextComponent message = new TextComponent(e.getMessage());
                               message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.chat("&cClick to report.")).create()));
                               message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + player.getUniqueId() + " " + e.getMessage()));

                               /*

                               if (!channelPlayer.hasPermission("sab.staff")) {

                               } else {
                                   message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.chat("&cClick to mute.")).create()));
                                   message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mute " + player.getName() + " "));
                               }

                               //todo fix, producing NPE at first if

                                */

                               try {
                                   channelPlayer.spigot().sendMessage(channel, username, barrier2, message);
                               } catch (NullPointerException ignored) {

                               }
                           }

                       }

                       if (c.equalsIgnoreCase("staff")) {
                           Bukkit.getOnlinePlayers().stream().filter(o -> o.hasPermission("sab.staff")).forEach(channelPlayer -> {
                               TextComponent channel = new TextComponent(Color.chat("&7[&cStaff&7] "));
                               channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Color.chat("&aAvailable channels:" +
                                       "\n&eGlobal" +
                                       "\n&cStaff &8- &7Current")).create()));

                               TextComponent username = new TextComponent(Color.chat(getPrefixes(player) + player.getDisplayName()));
                               TextComponent barrier2 = new TextComponent(Color.chat("&e >&f "));
                               TextComponent message = new TextComponent(e.getMessage());
                               message.setColor(ChatColor.YELLOW);
                               try {
                                   channelPlayer.spigot().sendMessage(channel, username, barrier2, message);
                               } catch (NullPointerException ignored) {

                               }
                           });

                       }
                   });
               });
               return;
           }

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
           });
        });
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