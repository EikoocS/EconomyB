package tech.cookiepower.economyb.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class EconomybApiPlugin extends JavaPlugin {
    public static Logger logger;
    public static Boolean registered = false;
    @Override
    public void onEnable() {
        logger = getLogger();
        if (EconomyB.hasAPI()){
            registered = true;
            logger.info("EconomyB API is register by "+EconomyB.getProvider());
        }else {
            registered = false;
            logger.warning("Nobody register EconomyB API, most of the features will be unavailable.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
