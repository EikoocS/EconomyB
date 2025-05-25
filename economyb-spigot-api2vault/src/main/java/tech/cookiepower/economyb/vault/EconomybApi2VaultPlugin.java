package tech.cookiepower.economyb.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.cookiepower.economyb.api.EconomybAPI;

import java.util.logging.Logger;

public final class EconomybApi2VaultPlugin extends JavaPlugin {
    public static Logger logger;
    public static Configuration config;

    @Override
    public void onLoad() {
        logger = getLogger();
        saveDefaultConfig();
        config = getConfig();
        var enable = config.getBoolean("enabled");
        if (enable) {
            var currency = config.getString("currency", "test");
            var servicesManager = getServer().getServicesManager();
            var economybAPI = servicesManager.getRegistration(EconomybAPI.class);
            if (economybAPI != null) {
                var vaultEconomy = new VaultEconomy(economybAPI.getProvider().getAccounts(),currency);
                logger.info("EconomyB API is register by " + economybAPI.getProvider());
                servicesManager.register(Economy.class, vaultEconomy, this, ServicePriority.Normal);
            } else {
                logger.warning("Nobody register EconomyB API, most of the features will be unavailable.");
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
