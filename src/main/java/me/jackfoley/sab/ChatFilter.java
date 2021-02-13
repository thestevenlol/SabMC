package me.jackfoley.sab;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ListIterator;

@CommandAlias("chatfilter|cf")
public class ChatFilter extends BaseCommand {

    // todo make whitelist & blacklist requests
    /*

    @Subcommand("whitelist request")
    @Description("Request for a word to be whitelisted")
    public void requestWhitelist(Player player, String[] args) {
        if (args.length == 0) player.sendMessage(Color.chat(Messages.prefix + "&c/cf whitelist request <word>"));
        if (args.length == 1) {

        }
        if (args.length > 1) player.sendMessage(Color.chat(Messages.prefix + "&c/cf whitelist request <word>"));
    }

     */

    @Subcommand("blacklist add")
    @CommandPermission("sab.admin")
    public void addBlacklist(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-words");
        for (String s : list) {
            if (word.equalsIgnoreCase(s)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is already in the blacklist."));
                return;
            }
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.add(word);
        Main.getPlugin().getConfig().set("bad-words", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been added to the blacklist."));
    }

    @Subcommand("blacklist remove")
    @CommandPermission("sab.admin")
    public void removeBlacklist(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-words");
        boolean exists = list.stream().anyMatch(word::equalsIgnoreCase);
        if (!exists) {
            player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is not in the blacklist."));
            return;
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.remove(word);
        Main.getPlugin().getConfig().set("bad-words", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been removed from the blacklist."));
    }

    @Subcommand("exclusion add")
    @CommandPermission("sab.admin")
    public void addExclusion(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        for (String s : list) {
            if (word.equalsIgnoreCase(s)) {
                player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is already in the exclusions list."));
                return;
            }
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.add(word);
        Main.getPlugin().getConfig().set("bad-word-exclusions", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been added to the exclusions list."));
    }

    @Subcommand("exclusion remove")
    @CommandPermission("sab.admin")
    public void removeExclusion(Player player, String[] args) {
        String word = args[0];
        List<String> list = Main.getPlugin().getConfig().getStringList("bad-word-exclusions");
        boolean exists = list.stream().anyMatch(word::equalsIgnoreCase);
        if (!exists) {
            player.sendMessage(Color.chat(Messages.prefix + "&cThe word " + Color.strip(word) + " is not in the exclusions list."));
            return;
        }
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.set(listIterator.next().toLowerCase());
        }
        list.remove(word);
        Main.getPlugin().getConfig().set("bad-word-exclusions", list);
        Main.getPlugin().saveConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&7The word " + Color.strip(word) + " has been removed from the exclusions list."));
    }

}
