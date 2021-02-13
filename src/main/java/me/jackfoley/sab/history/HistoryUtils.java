package me.jackfoley.sab.history;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Callback;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Formatting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HistoryUtils {

    public static void getMuteHistory(UUID uuid, Callback<List<String[]>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {

            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_mutes WHERE PLAYERUUID = ?")) {
                st.setString(1, uuid.toString());
                try (ResultSet set = st.executeQuery()) {
                    List<String[]> list = new ArrayList();
                    while (set.next()) {
                        String[] array = {set.getString(1), set.getString(2), String.valueOf(set.getInt(3)), set.getString(4)};
                        list.add(array);
                    }
                    callback.onQueryDone(list);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public static Inventory createMuteHistoryInventory(UUID uuid) {
        Inventory inventory = Bukkit.createInventory(null, 9, Color.chat("&cMute History: " + Bukkit.getOfflinePlayer(uuid).getName()));

        getMuteHistory(uuid, list -> {
            for (int i = 0; i < list.size(); i++) {
                String[] array = list.get(i);
                UUID muteduuid = UUID.fromString(array[0]);
                String reason = array[1];
                int duration = Integer.parseInt(array[2]);
                UUID muteruuid = UUID.fromString(array[3]);

                ItemStack item = new ItemStack(Material.NETHERITE_BLOCK);
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName("");
                List<String> lore = new ArrayList<>();
                lore.add(Color.chat("&cReason"));
                lore.add(Color.chat("&7-->&c " + reason));
                lore.add("");
                lore.add(Color.chat("&cDuration (Does not update in real-time.)"));
                if (duration != -1) {
                    lore.add(Color.chat((duration == 0 ? "&7-->&a " + Bukkit.getOfflinePlayer(muteduuid).getName() + " has finished their mute." : "&7--&c " + Formatting.time(duration))));
                } else {
                    lore.add(Color.chat("&7-->&c " + Bukkit.getOfflinePlayer(muteduuid).getName() + " is permanently muted."));
                }
                lore.add(" ");
                lore.add(Color.chat("&cMuted by " + Bukkit.getOfflinePlayer(muteruuid).getName()));
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        });
        return inventory;
    }

    public static Inventory createBanHistoryInventory(UUID uuid) {
        Inventory inventory = Bukkit.createInventory(null, 45, Color.chat("&cBan History: " + Bukkit.getOfflinePlayer(uuid).getName()));

        getBanHistory(uuid, list -> {
            for (int i = 0; i < list.size(); i++) {
                String[] array = list.get(i);
                UUID muteduuid = UUID.fromString(array[0]);
                String reason = array[1];
                int duration = Integer.parseInt(array[2]);
                UUID muteruuid = UUID.fromString(array[3]);

                ItemStack item = new ItemStack(Material.NETHERITE_BLOCK);
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName("");
                List<String> lore = new ArrayList<>();
                lore.add(Color.chat("&cReason"));
                lore.add(Color.chat("&7-->&c " + reason));
                lore.add("");
                lore.add(Color.chat("&cDuration (Does not update in real-time.)"));
                if (duration != -1) {
                    lore.add(Color.chat((duration == 0 ? "&7-->&a " + Bukkit.getOfflinePlayer(muteduuid).getName() + " has finished their ban." : "&7--&c " + Formatting.time(duration))));
                } else {
                    lore.add(Color.chat("&7-->&c " + Bukkit.getOfflinePlayer(muteduuid).getName() + " is permanently banned."));
                }
                lore.add(" ");
                lore.add(Color.chat("&cBanned by " + Bukkit.getOfflinePlayer(muteruuid).getName()));
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        });
        return inventory;
    }

    /*

    String[] array = history.get(i);
                UUID muteduuid = UUID.fromString(array[0]);
                String reason = array[1];
                int duration = Integer.parseInt(array[2]);
                UUID muteruuid = UUID.fromString(array[3]);
                System.out.println(muteduuid + ", " + muteruuid + ", " + duration + ", " + reason);

                ItemStack item = new ItemStack(Material.WHITE_WOOL);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Color.chat("&c" + Bukkit.getOfflinePlayer(muteruuid).getName() + " muted " + Bukkit.getOfflinePlayer(muteduuid).getName()));
                List<String> lore = new ArrayList<>();
                lore.add(Color.chat("&cReason"));
                lore.add(Color.chat("&7-->&c " + reason));
                lore.add("");
                lore.add(Color.chat("&cDuration (Does not update in real-time.)"));
                if (duration != -1) {
                    lore.add(Color.chat((duration == 0 ? "&7-->&a " + Bukkit.getOfflinePlayer(muteduuid).getName() + " has finished their mute." : "&7--&c " + Formatting.time(duration))));
                    return;
                }
                lore.add(Color.chat("&7-->&c " + Bukkit.getOfflinePlayer(muteduuid).getName() + " is permanently muted."));
                item.setItemMeta(meta);

     */

    public static void getBanHistory(UUID uuid, Callback<List<String[]>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {

            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ?")) {
                st.setString(1, uuid.toString());
                try (ResultSet set = st.executeQuery()) {
                    List<String[]> list = new ArrayList<>();
                    while (set.next()) {
                        String[] strings = {set.getString(1), set.getString(2), String.valueOf(set.getInt(3)), set.getString(4)};
                        list.add(strings);
                    }
                    callback.onQueryDone(list);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

}
