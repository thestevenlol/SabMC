package me.jackfoley.sab.playerinfo;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.sql.SQL;
import me.jackfoley.sab.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerInfoUtils {

    public static void setBasic(OfflinePlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String time = sdf.format(date);
            try {
                SQL sql = Main.getSql();
                PreparedStatement st = sql.createStatement("INSERT INTO sab_playerinfo (UUID,LASTONLINE,TELEPORTS,MESSAGES,CHANNEL) VALUES (?,?,?,?,?)");
                st.setString(1, player.getUniqueId().toString());
                st.setString(2, time);
                st.setBoolean(3, true);
                st.setBoolean(4, true);
                st.setString(5, "global");
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void getChannel(OfflinePlayer player, Callback<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                SQL sql = Main.getSql();
                PreparedStatement st = sql.createStatement("SELECT CHANNEL FROM sab_playerinfo WHERE UUID = ?");
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    if (set.next())
                    callback.onQueryDone(set.getString(1));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void getChannelPlayers(String channel, Callback<List<UUID>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                SQL sql = Main.getSql();
                PreparedStatement st = sql.createStatement("SELECT UUID FROM sab_playerinfo WHERE CHANNEL = ?");
                st.setString(1, channel);
                try (ResultSet set = st.executeQuery()) {
                    List<UUID> players = new ArrayList<>();
                    while (set.next()) {
                        players.add(UUID.fromString(set.getString(1)));
                    }
                    callback.onQueryDone(players);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void setChannel(OfflinePlayer player, String channel) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            Calendar cal = Calendar.getInstance();
            try {
                SQL sql = Main.getSql();
                PreparedStatement st = sql.createStatement("UPDATE sab_playerinfo SET CHANNEL = ? WHERE UUID = ?");
                st.setString(1, channel);
                st.setString(2, player.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void exists(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            SQL sql = Main.getSql();
            PreparedStatement st = sql.createStatement("SELECT * FROM sab_playerinfo WHERE UUID = ?");
            try {
                st.setString(1, player.getUniqueId().toString());
                ResultSet set = st.executeQuery();
                boolean exists = set.next();
                set.close();
                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> callback.onQueryDone(exists));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void messagesEnabled(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT MESSAGES FROM sab_playerinfo WHERE UUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    if (set.next()) {
                        callback.onQueryDone(set.getBoolean(1));
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void teleportsEnabled(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT TELEPORTS FROM sab_playerinfo WHERE UUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    if (set.next()) {
                        callback.onQueryDone(set.getBoolean(1));
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

}
