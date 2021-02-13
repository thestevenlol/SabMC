package me.jackfoley.sab.teleporting;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Callback;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeleportUtils {

    public static void setTeleport(Player sender, Player receiver, TeleportType type) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {

            senderExists(sender, exists -> {
                if (exists) {
                    try (PreparedStatement st = Main.getSql().createStatement("UPDATE sab_teleports SET RECEIVERUUID = ?, TYPE = ? WHERE SENDERUUID = ? ")) {
                        st.setString(1, receiver.getUniqueId().toString());
                        st.setString(2, type.toString());
                        st.setString(3, sender.getUniqueId().toString());
                        st.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_teleports (SENDERUUID,RECEIVERUUID,TYPE) VALUE (?,?,?)")) {
                    st.setString(1, sender.getUniqueId().toString());
                    st.setString(2, receiver.getUniqueId().toString());
                    st.setString(3, type.toString());
                    st.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });


        });
    }

    public static void getTeleportType(OfflinePlayer player, Callback<TeleportType> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("SELECT TYPE FROM sab_teleports WHERE RECEIVERUUID = ?")) {
               st.setString(1, player.getUniqueId().toString());
               try (ResultSet set = st.executeQuery()) {
                   set.next();
                   callback.onQueryDone(TeleportType.valueOf(set.getString(1)));
               }
           } catch (SQLException e) {
               player.getPlayer().sendMessage(Color.chat(Messages.prefix + "&cYou do not have a pending teleport request."));
           }
        });
    }

    public static void getTeleportSender(OfflinePlayer player, Callback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT SENDERUUID FROM sab_teleports WHERE RECEIVERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    set.next();
                    callback.onQueryDone(set.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getTeleportReceiver(OfflinePlayer player, Callback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT RECEIVERUUID FROM sab_teleports WHERE SENDERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    set.next();
                    callback.onQueryDone(set.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void senderExists(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_teleports WHERE SENDERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void receiverExists(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_teleports WHERE RECEIVERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void closeTeleport(OfflinePlayer sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("DELETE FROM sab_teleports WHERE SENDERUUID = ?")) {
                st.setString(1, sender.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
