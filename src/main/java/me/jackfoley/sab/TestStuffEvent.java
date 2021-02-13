package me.jackfoley.sab;

import me.jackfoley.sab.sql.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TestStuffEvent implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e)  {
        Player player = e.getPlayer();
        banned(player.getUniqueId()).thenAccept(banned -> {
            if (banned) {
                player.sendMessage("banned");
                return;
            }
            player.sendMessage("not banned");
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    // Basic method that returns a ResultSet wrapped in a CompletableFuture, just if you want to return a String instead of ResultSet, change ResultSet to String
    public CompletableFuture<Boolean> banned(final UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            // Try-with-resources so the statement and connection get automatically closed once done
            try (PreparedStatement preparedStatement = Main.getSql().createStatement("SELECT * FROM sab_bans WHERE PLAYERUUID = ? AND DURATION > 0 OR DURATION = -1")) {
                preparedStatement.setString( 1, uuid.toString() );
                try ( ResultSet resultSet = preparedStatement.executeQuery() ) {
                    return resultSet.next();
                }
            } catch ( SQLException e ) {
                return null;
            }
        });
    }

}
