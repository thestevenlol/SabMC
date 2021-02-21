package me.jackfoley.sab.util;

import me.jackfoley.sab.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LastLocationUtils {

    public static void exists(UUID uuid, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_lastlocations WHERE UUID = ?")) {
                st.setString(1, uuid.toString());
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void insert(UUID uuid, double x, double y, double z, float yaw, float pitch, String world) {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_lastlocations (UUID,X,Y,Z,YAW,PITCH,WORLD) VALUES (?,?,?,?,?,?,?)")) {
                st.setString(1, uuid.toString());
                st.setDouble(2, x);
                st.setDouble(3, y);
                st.setDouble(4, z);
                st.setFloat(5, yaw);
                st.setFloat(6, pitch);
                st.setString(7, world);
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void update(UUID uuid, double x, double y, double z, float yaw, float pitch, String world) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("UPDATE sab_lastlocations SET X = ?, Y = ?, Z = ?, YAW = ?, PITCH = ?, WORLD = ? WHERE UUID = ?")) {
                st.setDouble(1, x);
                st.setDouble(2, y);
                st.setDouble(3, z);
                st.setFloat(4, yaw);
                st.setFloat(5, pitch);
                st.setString(6, world);
                st.setString(7, uuid.toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getLastLocation(UUID uuid, Callback<Location> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_lastlocations WHERE UUID = ?")) {
                st.setString(1, uuid.toString());
                try (ResultSet set = st.executeQuery()) {
                    if (set.next()) {
                        Location location = new Location(
                                Bukkit.getWorld(set.getString(7)),
                                set.getDouble(2),
                                set.getDouble(3),
                                set.getDouble(4),
                                set.getFloat(5),
                                set.getFloat(6)
                        );
                        callback.onQueryDone(location);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
