package tech.cookiepower.economyb.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class EconomybApiPlugin extends JavaPlugin {
    public static Logger logger;
    @Override
    public void onEnable() {
        logger = getLogger();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
