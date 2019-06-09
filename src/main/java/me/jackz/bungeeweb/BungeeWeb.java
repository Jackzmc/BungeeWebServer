package me.jackz.bungeeweb;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class BungeeWeb extends Plugin {

    private static File CONFIG_FILE;
    private static Configuration config;

    @Override
    public void onEnable() {
        CONFIG_FILE = new File(getDataFolder(), "config.yml");
        config = setupConfig();

        if(config != null && config.getBoolean("server.enabled")) {
            int port = config.getInt("server.port");
            String host = config.getString("server.ip");
            getLogger().info(String.format("Starting WebServer on %s:%d",host,port));
            WebServer server = new WebServer();
            try {
                server.startServer(this,host,port);
            } catch (IOException e) {
                getLogger().warning("WebServer: " + e.getMessage());
            }
        }
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private Configuration setupConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();
        if (!CONFIG_FILE.exists()) {
            try {
                Configuration config = new Configuration();
                config.set("server.enabled",true);
                config.set("server.ip","127.0.0.1");
                config.set("server.port",8080);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, CONFIG_FILE);
            }catch(IOException e) {
                getLogger().severe("Failed to setup config file. Disabling...");
                getLogger().severe(e.getMessage());
                this.onDisable();
                return null;
            }
        }

        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(CONFIG_FILE);
            if(!config.contains("server.enabled")) config.set("server.enabled",true);
            if(!config.contains("server.ip")) config.set("server.ip","127.0.0.1");
            if(!config.contains("server.port")) config.set("server.port",8080);
            return config;
        } catch (IOException e) {
            getLogger().severe("Could not load config file, disabling...");
            this.onDisable();
            return null;
        }
    }
}
