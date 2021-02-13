package me.jackfoley.sab;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import me.jackfoley.sab.util.Color;
import me.jackfoley.sab.util.Messages;
import org.bukkit.entity.Player;

@CommandAlias("sab")
@CommandPermission("sab.admin")
public class SabMC extends BaseCommand {

    @Subcommand("reload")
    @Description("Reload the plugin's files.")
    public void reload(Player player) {
        Main.getPlugin().reloadConfig();
        player.sendMessage(Color.chat(Messages.prefix + "&aReloaded all Yaml files."));
    }

}
