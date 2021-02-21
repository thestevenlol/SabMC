package me.jackfoley.sab.homes;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeUtils {

    public HomeUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void deleteHome(String homeName, OfflinePlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("DELETE FROM sab_homes WHERE PLAYERUUID = ? AND NAME = ?")) {
               st.setString(1, player.getUniqueId().toString());
               st.setString(2, homeName);
               st.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public static void hasAnyHomes(OfflinePlayer player, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_homes WHERE PLAYERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void getHomes(OfflinePlayer player, Callback<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT NAME FROM sab_homes WHERE PLAYERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    List<String> homes = new ArrayList<>();
                    while (set.next()) {
                        homes.add(set.getString(1));
                    }
                    callback.onQueryDone(homes);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void getHomeLocation(OfflinePlayer player, String home, Callback<Location> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_homes WHERE PLAYERUUID = ? AND NAME = ?")) {
                st.setString(1, player.getUniqueId().toString());
                st.setString(2, home);
                try (ResultSet set = st.executeQuery()) {
                    if (set.next()) {
                        Location loc = new Location(
                                Bukkit.getWorld(set.getString(7)),
                                set.getDouble(2),
                                set.getDouble(3),
                                set.getDouble(4),
                                set.getFloat(5),
                                set.getFloat(6)
                        );
                        set.close();
                        callback.onQueryDone(loc);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void setHome(Player player, String home) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
           try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_homes (PLAYERUUID,X,Y,Z,YAW,PITCH,WORLD,NAME) VALUES (?,?,?,?,?,?,?,?)")) {
               st.setString(1, player.getUniqueId().toString());
               st.setDouble(2, player.getLocation().getX());
               st.setDouble(3, player.getLocation().getY());
               st.setDouble(4, player.getLocation().getZ());
               st.setFloat(5, player.getLocation().getYaw());
               st.setFloat(6, player.getLocation().getPitch());
               st.setString(7, player.getWorld().getName());
               st.setString(8, home);
               st.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    public static void certainHomeExists(OfflinePlayer player, String home, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_homes WHERE PLAYERUUID = ? AND NAME = ?")) {
                st.setString(1, player.getUniqueId().toString());
                st.setString(2, home);
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void getTotalHomes(OfflinePlayer player, Callback<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_homes WHERE PLAYERUUID = ?")) {
                st.setString(1, player.getUniqueId().toString());
                try (ResultSet set = st.executeQuery()) {
                    List<String> homes = new ArrayList<>();
                    set.last();
                    callback.onQueryDone(set.getRow());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

}
