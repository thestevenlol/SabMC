package me.jackfoley.sab.loops;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.moderation.ModerationUtils;
import me.jackfoley.sab.sql.SQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BanLoop {

    public static void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getPlugin(), () -> {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                ModerationUtils.tempbanned(player).thenAccept(banned -> {
                    if (banned) {
                        SQL sql = Main.getSql();
                        PreparedStatement getDur = sql.createStatement("SELECT DURATION FROM sab_bans WHERE PLAYERUUID=? AND DURATION>0");
                        try {
                            getDur.setString(1, player.getUniqueId().toString());
                            ResultSet set = getDur.executeQuery();
                            int duration = 0;
                            if (set.next()) {
                                duration = set.getInt(1);
                                set.close();
                            }

                            if (duration == -1) return;

                            PreparedStatement st = sql.createStatement("UPDATE sab_bans SET DURATION=? WHERE PLAYERUUID=? AND DURATION>0");
                            st.setInt(1, duration - 1);
                            st.setString(2, player.getUniqueId().toString());
                            st.executeUpdate();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }).exceptionally(throwable -> {
                   throwable.printStackTrace();
                   return null;
                });
            }
        }, 0, 20);
    }

}
