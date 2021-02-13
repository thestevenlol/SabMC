package me.jackfoley.sab.sql;

import lombok.SneakyThrows;
import me.jackfoley.sab.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL {

    private final String host = Main.getPlugin().getConfig().getString("database.host");
    private final int port = Main.getPlugin().getConfig().getInt("database.port");
    private final String database = Main.getPlugin().getConfig().getString("database.database");
    private final String username = Main.getPlugin().getConfig().getString("database.username");
    private final String password = Main.getPlugin().getConfig().getString("database.password");
    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
                System.out.println("Connected to database successfully.");
            } catch (SQLException e) {
                System.out.println("Plugin disabling due to an error. Is the SQL server running?");
                System.out.println("If you want to see the error, please change the sql-debug-on-launch value in config to true.");
                if (Main.getPlugin().getConfig().getBoolean("sql-debug-on-launch")) e.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(Main.getPlugin());
            }
        }
    }

    @SneakyThrows
    public void disconnect() {
        if (isConnected()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @SneakyThrows
    public PreparedStatement createStatement(String sql) {
        return this.connection.prepareStatement(sql);
    }

}
