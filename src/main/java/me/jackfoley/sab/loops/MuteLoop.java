package me.jackfoley.sab.loops;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.moderation.ModerationUtils;
import me.jackfoley.sab.sql.SQL;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MuteLoop {

    public static void run() {
        SQL sql = Main.getSql();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getPlugin(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                ModerationUtils.muted(player, muted -> {
                    try (PreparedStatement getDur = sql.createStatement("SELECT DURATION FROM sab_mutes WHERE PLAYERUUID=? AND DURATION>0")) {
                        getDur.setString(1, player.getUniqueId().toString());
                        ResultSet set = getDur.executeQuery();
                        int duration = 0;
                        if (set.next()) {
                            duration = set.getInt(1);
                            set.close();
                        }

                        if (duration == -1) return;

                        try (PreparedStatement st = sql.createStatement("UPDATE sab_mutes SET DURATION=? WHERE PLAYERUUID=? AND DURATION>0")) {
                            st.setInt(1, duration - 1);
                            st.setString(2, player.getUniqueId().toString());
                            st.executeUpdate();
                        } catch (SQLException ee) {
                            ee.printStackTrace();
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            });
        }, 0, 20);
    }

}
