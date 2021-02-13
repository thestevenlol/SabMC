package me.jackfoley.sab.messaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MessageManager {

    public static HashMap<UUID, UUID> messageLink = new HashMap<>();

    public static void setReplyTarget(Player messenger, Player receiver) {
        messageLink.put(receiver.getUniqueId(), messenger.getUniqueId());
    }

    public static Player getReplyTarget(Player player) {
        return Bukkit.getPlayer(messageLink.get(player.getUniqueId()));
    }
}
