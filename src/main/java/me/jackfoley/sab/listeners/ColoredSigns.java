package me.jackfoley.sab.listeners;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColoredSigns implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onSignCreate(final SignChangeEvent e) {
        Player player = e.getPlayer();
        List<String> words = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String string = e.getLine(i);
            words.addAll(Arrays.asList(string.split("\\s+")));
        }

        if (detectedBadWord(player, words)) e.setCancelled(true);

        if (player.hasPermission("sab.signcolor")) {
            for (int i = 0; i < 4; i++) {
                if (e.getLine(i) == null) return;
                e.setLine(i, Color.chat(e.getLine(i)));
            }
        }
        words.clear();
    }

    private boolean detectedBadWord(Player player, List<String> words) {
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
                    player.sendMessage(Color.chat(Messages.prefix + "&cYou are not allowed to put " + Color.strip(word) + " on a sign as it contains the blacklisted word: " + badWord));
                    break;

                }
            }
        }
        return detected;
    }

}
