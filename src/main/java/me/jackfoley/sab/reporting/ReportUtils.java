package me.jackfoley.sab.reporting;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Callback;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReportUtils {

    public static List<String> condenseLore(String string) {
        StringBuilder sb = new StringBuilder(string);
        int i = 0;
        while (i + 35 < sb.length() && (i = sb.lastIndexOf(" ", i + 35)) != -1) {
            sb.replace(i, i + 1, Color.chat("\n&c"));
        }
        List<String> list = new ArrayList<>();
        list.add(Color.chat("&8------------------"));
        String[] a = sb.toString().split("\n");
        list.addAll(Arrays.asList(a));
        list.add(Color.chat("&8------------------"));
        return list;
    }

    public static void reportAlreadyExists(UUID uuidReportee, String message, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_reports WHERE PLAYERUUID = ? AND MESSAGE = ?")) {
                st.setString(1, uuidReportee.toString());
                st.setString(2, message);
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getMessage(UUID uuid, Callback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT MESSAGE FROM sab_reports WHERE PLAYERUUID = ?")) {
                st.setString(1, uuid.toString());
                try (ResultSet set = st.executeQuery()) {
                    set.next();
                    callback.onQueryDone(set.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getReporter(String message, Callback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT REPORTERUUID FROM sab_reports WHERE MESSAGE = ?")) {
                st.setString(1, message);
                try (ResultSet set = st.executeQuery()) {
                    set.next();
                    callback.onQueryDone(set.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static Inventory createSendReportConfirmInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, Color.chat("&cReport"));
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta confirmM = confirm.getItemMeta();
        ItemMeta cancelM = cancel.getItemMeta();
        confirmM.setDisplayName(Color.chat("&aConfirm"));
        cancelM.setDisplayName(Color.chat("&cCancel"));
        confirm.setItemMeta(confirmM);
        cancel.setItemMeta(cancelM);
        inventory.setItem(3, confirm);
        inventory.setItem(5, cancel);
        return inventory;
    }

    public static Inventory createDoReportConfirmInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, Color.chat("&cConfirm report?"));
        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta confirmM = confirm.getItemMeta();
        ItemMeta cancelM = cancel.getItemMeta();
        confirmM.setDisplayName(Color.chat("&aConfirm"));
        cancelM.setDisplayName(Color.chat("&cDeny"));
        confirm.setItemMeta(confirmM);
        cancel.setItemMeta(cancelM);
        inventory.setItem(3, confirm);
        inventory.setItem(5, cancel);
        return inventory;
    }

    public static Inventory createReportsInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, Color.chat("&cReports"));

        getAllReports(list -> {
            for (int i = 0; i < list.size(); i++) {
                String[] array = list.get(i);
                UUID reportedUUID = UUID.fromString( array[0]);
                String message = array[1];
                UUID reporterUUID = UUID.fromString(array[2]);
                List<String> lore = condenseLore(Color.chat(message));

                int finalI = i;
                getSkull(Bukkit.getOfflinePlayer(reportedUUID), skull -> {
                    ItemMeta meta = skull.getItemMeta();
                    meta.setDisplayName(Color.chat("&c" + Bukkit.getOfflinePlayer(reportedUUID).getName()));
                    meta.setLore(lore);
                    meta.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), "uuid"), PersistentDataType.STRING, reportedUUID.toString());
                    meta.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), "reporteruuid"), PersistentDataType.STRING, reporterUUID.toString());
                    meta.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), "message"), PersistentDataType.STRING, message);
                    skull.setItemMeta(meta);
                    inventory.setItem(finalI, skull);
                });


            }
        });

        return inventory;
    }

    public static void getAllReports(Callback<List<String[]>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_reports")) {
               try (ResultSet set = st.executeQuery()) {
                   List<String[]> list = new ArrayList();
                   while (set.next()) {
                       String[] array = {set.getString(1), set.getString(2), set.getString(3)};
                       list.add(array);
                   }
                   callback.onQueryDone(list);
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public static void setReport(UUID uuidReporter, UUID uuidReportee, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_reports (PLAYERUUID,MESSAGE,REPORTERUUID) VALUES (?,?,?)")) {
                st.setString(1, uuidReportee.toString());
                st.setString(2, message);
                st.setString(3, uuidReporter.toString());
                st.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public static void getSkull(OfflinePlayer player, Callback<ItemStack> callback) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        skull.setItemMeta(meta);
        callback.onQueryDone(skull);
    }

    public static void removeReports(UUID reportedUUID) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("DELETE FROM sab_reports WHERE PLAYERUUID = ?")) {
               st.setString(1, reportedUUID.toString());
               st.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public static void deleteSingle(UUID reported, UUID reporter, String message) {
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("DELETE FROM sab_reports WHERE PLAYERUUID = ? AND REPORTERUUID = ? AND MESSAGE = ?")) {
                st.setString(1, reported.toString());
                st.setString(2, reporter.toString());
                st.setString(3, message);
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void doReport(Player sender, OfflinePlayer reporter, OfflinePlayer target) {
        new AnvilGUI.Builder().onComplete(((player, s) -> {
            if (reporter.isOnline()) reporter.getPlayer().sendMessage(Color.chat(Messages.prefix + "&7Your report against " + target.getName() + " has been handled by " + sender.getName() + "."));
            Bukkit.dispatchCommand(sender, "mute " + target.getName() + " " + s);
            removeReports(target.getUniqueId());
            return AnvilGUI.Response.close();
        })).preventClose()
                .title("Mute Player")
                .itemLeft(new ItemStack(Material.PAPER))
                .text("Mute reason")
                .plugin(Main.getPlugin())
                .open(sender);
    }

}
