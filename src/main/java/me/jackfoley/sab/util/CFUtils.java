package me.jackfoley.sab.util;

import me.jackfoley.sab.Main;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CFUtils {

    public static void exclusionRequestExists(String word, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_requests WHERE TYPE = 'exclusion' AND word = ?")) {
                st.setString(1, word);
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getExclusionRequests(Callback<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_requests WHERE TYPE = 'exclusion'")) {
                try (ResultSet set = st.executeQuery()) {
                    List<String> list = new ArrayList<>();
                    while (set.next()) {
                        list.add(set.getString(3));
                    }
                    callback.onQueryDone(list);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void remove(String type, String word) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("DELETE FROM sab_requests WHERE TYPE = ? AND WORD = ?")) {
                st.setString(1, type);
                st.setString(2, word);
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getBlacklistRequests(Callback<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_requests WHERE TYPE = 'blacklist'")) {
                try (ResultSet set = st.executeQuery()) {
                    List<String> list = new ArrayList<>();
                    while (set.next()) {
                        list.add(set.getString(3));
                    }
                    callback.onQueryDone(list);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void blacklistRequestExists(String word, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_requests WHERE TYPE = 'blacklist' AND word = ?")) {
                st.setString(1, word);
                try (ResultSet set = st.executeQuery()) {
                    callback.onQueryDone(set.next());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
