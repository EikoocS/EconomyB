package tech.cookiepower.economyb;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.cookiepower.economyb.api.EconomybAPI;

import java.util.logging.Logger;

public final class EconomybPlugin extends JavaPlugin {
    public static Logger logger;
    private static EconomybApiImpl instance;
    private static Configuration config;

    @Override
    public void onLoad() {
        logger = getLogger();
        saveDefaultConfig();
        config = getConfig();

        var manager = Bukkit.getServicesManager();
        var registration = manager.getRegistration(EconomybAPI.class);
        if (registration != null) {
            logger.warning("Economyb API already registered!");
        }else{
            var databaseConfig = new EconomybConfig();
            databaseConfig.host = config.getString("host", "localhost");
            databaseConfig.port = config.getInt("port", 3306);
            databaseConfig.user = config.getString("user", "root");
            databaseConfig.password = config.getString("password", "");
            databaseConfig.database = config.getString("database", "");
            instance = new EconomybApiImpl(databaseConfig);
            manager.register(EconomybAPI.class,instance,this, ServicePriority.Normal);
        }
    }

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable() {

    }
}
