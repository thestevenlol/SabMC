package me.jackfoley.sab.listeners;

import me.jackfoley.sab.Main;
import me.jackfoley.sab.moderation.ModerationUtils;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Formatting;
import me.jackfoley.sab.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BanListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) throws SQLException {

        PreparedStatement st1 = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ? AND DURATION = -1");
        st1.setString(1, e.getUniqueId().toString());
        ResultSet set1 = st1.executeQuery();
        try {
            boolean banned = set1.next();
            String reason = set1.getString(2);
            String bannerUUID = set1.getString(4);
            set1.close();

            if (banned) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.chat(Messages.banMessage(Bukkit.getOfflinePlayer(UUID.fromString(bannerUUID)), reason)));
                return;
            }
        } catch (SQLException ignored) {}


        PreparedStatement st2 = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ? AND DURATION > 0");
        st2.setString(1, e.getUniqueId().toString());
        ResultSet set2 = st2.executeQuery();
        try {
            boolean tempBanned = set2.next();
            String reasonTempBanned = set2.getString(2);
            int duration = set2.getInt(3);
            String bannerUUIDTempBanned = set2.getString(4);
            set2.close();

            if (tempBanned) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.chat(Messages.tempBanMessage(Bukkit.getOfflinePlayer(UUID.fromString(bannerUUIDTempBanned)), reasonTempBanned, Formatting.time(duration))));
            }
        } catch (SQLException ignored) {}


        /*

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            ModerationUtils.banned(Bukkit.getPlayer(e.getUniqueId())).thenAccept(banned -> {
                if (banned) {
                    ModerationUtils.getBanReason(Bukkit.getPlayer(e.getUniqueId())).thenAccept(reason -> {
                        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Color.chat(Messages.banMessage(player, reason)));
                        });
                    }).exceptionally(throwable -> {
                        throwable.printStackTrace();
                        return null;
                    });
                }
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        }, 1);

         */
    }
}
