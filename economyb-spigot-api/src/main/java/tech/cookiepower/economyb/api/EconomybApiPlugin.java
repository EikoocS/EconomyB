package tech.cookiepower.economyb.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class EconomybApiPlugin extends JavaPlugin {
    public static Logger logger;
    @Override
    public void onEnable() {
        logger = getLogger();
        var accountsApi = Bukkit.getServicesManager().getRegistration(EconomybAPI.class).getProvider().getAccounts();
        var executor = new EconomyCommandExecutor(accountsApi);
        Bukkit.getPluginCommand("economy").setExecutor(executor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
