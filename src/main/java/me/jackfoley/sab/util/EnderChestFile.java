package me.jackfoley.sab.util;

import lombok.SneakyThrows;
import me.jackfoley.sab.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class EnderChestFile {

    private File file;
    private YamlConfiguration config;

    @SneakyThrows
    public void setup(Main plugin) {
        file = new File(plugin.getDataFolder(), "enderchests.yml");
        config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    @SneakyThrows
    public void saveFile() {
        config.save(file);
    }

}
