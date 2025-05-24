package tech.cookiepower.economyb.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.cookiepower.economyb.api.EconomybAPI;

import java.util.logging.Logger;

public final class EconomybApi2VaultPlugin extends JavaPlugin {
    public static Logger logger;

    @Override
    public void onLoad() {
        logger = getLogger();
        var servicesManager = getServer().getServicesManager();
        var economybAPI = servicesManager.getRegistration(EconomybAPI.class);
        if (economybAPI != null) {
            var vaultEconomy = new VaultEconomy(economybAPI.getProvider().getAccounts(),"test");
            logger.info("EconomyB API is register by " + economybAPI.getProvider());
            servicesManager.register(Economy.class, vaultEconomy, this, ServicePriority.Normal);
        } else {
            logger.warning("Nobody register EconomyB API, most of the features will be unavailable.");
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
