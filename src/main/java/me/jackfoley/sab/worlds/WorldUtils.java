package me.jackfoley.sab.worlds;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorldUtils {

    public static void getHubLocation(String hubName, Callback<Location> callback ) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_hubs WHERE NAME = ?")) {
                st.setString(1, hubName);
                try (ResultSet set = st.executeQuery()) {
                    if (set.next()) {
                        double x = set.getDouble(1);
                        double y = set.getDouble(2);
                        double z = set.getDouble(3);
                        float yaw = set.getFloat(4);
                        float pitch = set.getFloat(5);
                        String world = set.getString(6);

                        callback.onQueryDone(new Location(
                           Bukkit.getWorld(world),
                           x,
                           y,
                           z,
                           yaw,
                           pitch
                        ));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void setHubLocation(Location location, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            hubExists(name, exists -> {
                if (!exists) {
                    try (PreparedStatement st = Main.getSql().createStatement("INSERT INTO sab_hubs (X,Y,Z,YAW,PITCH,WORLD,NAME) VALUES (?,?,?,?,?,?,?)")) {
                        st.setDouble(1, location.getX());
                        st.setDouble(2, location.getY());
                        st.setDouble(3, location.getZ());
                        st.setFloat(4, location.getYaw());
                        st.setFloat(5, location.getPitch());
                        st.setString(6, location.getWorld().getName());
                        st.setString(7, name);
                        st.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    try (PreparedStatement st = Main.getSql().createStatement("UPDATE sab_hubs SET X = ?, Y = ?, Z = ?, YAW = ?, PITCH = ?, WORLD = ? WHERE NAME = ?")) {
                        st.setDouble(1, location.getX());
                        st.setDouble(2, location.getY());
                        st.setDouble(3, location.getZ());
                        st.setFloat(4, location.getYaw());
                        st.setFloat(5, location.getPitch());
                        st.setString(6, location.getWorld().getName());
                        st.setString(7, name);
                        st.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public static void hubExists(String hub, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_hubs WHERE NAME = ?")) {
                st.setString(1, hub);
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
