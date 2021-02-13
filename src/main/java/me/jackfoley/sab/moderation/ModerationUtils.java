package me.jackfoley.sab.moderation;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.sql.SQL;
import me.jackfoley.sab.util.Callback;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Formatting;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModerationUtils {

    public static CompletableFuture<Integer> getMuteDuration(OfflinePlayer player) {
        return CompletableFuture.supplyAsync( () -> {
            try (PreparedStatement preparedStatement = Main.getSql().createStatement("SELECT DURATION FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1")) {
                preparedStatement.setString( 1, player.getUniqueId().toString() );
                try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                    resultSet.next();
                    return resultSet.getInt(1);
                }
            } catch ( SQLException e ) {
                return null;
            }
        });
    }

    /*
    String uuid = player.getUniqueId().toString();
        SQL sql = Main.getSql();
        PreparedStatement st = sql.createStatement("SELECT * FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1");
        st.setString(1, uuid);
        ResultSet set = st.executeQuery();
        return set.next();
     */

    public static void muted(OfflinePlayer player, Callback<Boolean> callback) {
        try (PreparedStatement preparedStatement = Main.getSql().createStatement("SELECT * FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1")) {
            preparedStatement.setString( 1, player.getUniqueId().toString() );
            try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                callback.onQueryDone(resultSet.next());
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Integer> totalMutes(OfflinePlayer player) {
        return CompletableFuture.supplyAsync( () -> {
            try (PreparedStatement preparedStatement = Main.getSql().createStatement("SELECT * FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION >= 0 OR DURATION = -1")) {
                preparedStatement.setString( 1, player.getUniqueId().toString() );
                try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                    resultSet.last();
                    return resultSet.getRow();
                }
            } catch ( SQLException e ) {
                return null;
            }
        });
    }

    public static void handleMute(SQL sql, OfflinePlayer player, String reason, int duration, Player sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            PreparedStatement st = sql.createStatement("INSERT INTO sab_mutes (PLAYERUUID,REASON,DURATION,MUTERUUID) VALUES (?,?,?,?)");
            try {
                st.setString(1, player.getUniqueId().toString());
                st.setString(2, reason);
                st.setInt(3, duration);
                st.setString(4, sender.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static CompletableFuture<String> getMuter(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
           try (PreparedStatement st = Main.getSql().createStatement("SELECT MUTERUUID FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1")) {
               st.setString(1, player.getUniqueId().toString());
               try (ResultSet set = st.executeQuery()) {
                   set.next();
                   return set.getString(1);
               }
           } catch (SQLException throwables) {
               throwables.printStackTrace();
               return null;
           }
        });
    }

    public static void unmute(SQL sql, OfflinePlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                PreparedStatement st = sql.createStatement("DELETE FROM sab_mutes WHERE PLAYERUUID=? AND DURATION > 0 OR DURATION = -1");
                st.setString(1, player.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static CompletableFuture<String> getMuteReason(OfflinePlayer player) {
        return CompletableFuture.supplyAsync( () -> {
            try (PreparedStatement preparedStatement = Main.getSql().createStatement("SELECT REASON FROM sab_mutes WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1")) {
                preparedStatement.setString( 1, player.getUniqueId().toString() );
                try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                    resultSet.next();
                    return resultSet.getString(1);
                }
            } catch ( SQLException e ) {
                return null;
            }
        });
    }

    public static CompletableFuture<Boolean> banned(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ? AND DURATION = -1 or DURATION > 0")) {
                    st.setString(1, player.getUniqueId().toString());
                    try (ResultSet set = st.executeQuery()) {
                        return set.next();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<Boolean> tempbanned(OfflinePlayer player)  {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ? AND DURATION > 0")) {
                    st.setString(1, player.getUniqueId().toString());
                    try (ResultSet set = st.executeQuery()) {
                        return set.next();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
    }

    public static void ban(OfflinePlayer player, String reason, Player sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            SQL sql = Main.getSql();
            PreparedStatement statement = sql.createStatement("INSERT INTO sab_bans (PLAYERUUID,REASON,DURATION,BANNERUUID) VALUES (?,?,?,?)");
            try {
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, reason);
                statement.setInt(3, -1);
                statement.setString(4, sender.getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        if (player.isOnline()) {
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                player.getPlayer().kickPlayer(Color.chat(Messages.banMessage(sender, reason)));
            });
        }
    }

    public static void tempban(OfflinePlayer player, String reason, Player sender, int duration) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            SQL sql = Main.getSql();
            PreparedStatement statement = sql.createStatement("INSERT INTO sab_bans (PLAYERUUID,REASON,DURATION,BANNERUUID) VALUES (?,?,?,?)");
            try {
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, reason);
                statement.setInt(3, duration);
                statement.setString(4, sender.getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        if (player.isOnline()) {
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                player.getPlayer().kickPlayer(Color.chat(Messages.tempBanMessage(sender, reason, Formatting.time(duration))));
            });
        }
    }

    public static CompletableFuture<String> getBanReason(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement getDur = Main.getSql().createStatement("SELECT REASON FROM sab_bans WHERE PLAYERUUID=? AND DURATION > 0 OR DURATION = -1")) {
                getDur.setString(1, player.getUniqueId().toString());
                try (ResultSet set = getDur.executeQuery()) {
                    set.next();
                    return set.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<Integer> getBanDuration(OfflinePlayer player) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement getDur = Main.getSql().createStatement("SELECT DURATION FROM sab_bans WHERE PLAYERUUID=? AND DURATION > 0 OR DURATION = -1")) {
                getDur.setString(1, player.getUniqueId().toString());
                try (ResultSet set = getDur.executeQuery()) {
                    set.next();
                    return set.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static void unban(OfflinePlayer player) {
        SQL sql = Main.getSql();
        PreparedStatement st = sql.createStatement("DELETE FROM sab_bans WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1");
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try {
                st.setString(1, player.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void kick(Player player, Player kicker, String reason) {
        player.kickPlayer(Color.chat(Messages.kickMessage(kicker, reason)));
    }

    public static void getPlayersViaIP(String ip, Callback<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            try (PreparedStatement st = Main.getSql().createStatement("SELECT * FROM sab_playerinfo WHERE IP = ?")) {
                st.setString(1, ip);
                try (ResultSet set = st.executeQuery()) {
                    List<String> a = new ArrayList<>();
                    while (set.next()) {
                        a.add(set.getString(1));
                    }
                    callback.onQueryDone(a);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

}
